package com.spring.clould.batch.config;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.entity.enums.BhJobStatusEnum;
import com.spring.clould.batch.entity.enums.YesOrNoEnum;
import com.spring.clould.batch.mapper.BhJobMapper;
import com.spring.clould.batch.util.BeanUtil;
import com.spring.clould.batch.util.ConvertUtil;
import com.spring.clould.batch.util.DateUtil;
import com.spring.clould.batch.util.IPUtil;
import com.spring.clould.batch.util.RedisLockUtil;

/**
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@Component
public class QuartzJobConfig implements Job {
	private Logger logger = LoggerFactory.getLogger(QuartzJobConfig.class);

	@Autowired
	BhJobMapper bhJobMapper;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	RedisLockUtil redisLockUtil;
	
	@Autowired
	JobRepository jobRepository;

	@Override
	public void execute(JobExecutionContext executorContext) throws JobExecutionException {
		JobDataMap jobDataMap = executorContext.getMergedJobDataMap();
		BhJob job = ConvertUtil.convertToBhJob(jobDataMap);
		// 获取分布式锁
		boolean isLock = redisLockUtil.lockJob(job.getJobName());
		if (isLock) {
			//重新获取一下数据库里的job信息
			job = bhJobMapper.selectById(job.getId());
			//设置任务执行参数
			JobParameters jobParameters = null;
			String jobInstanceId = job.getJobInstanceId();
			if (BhJobStatusEnum.FAILED.equals(job.getStatus())) {
				jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
			} else if (BhJobStatusEnum.STARTING.equals(job.getStatus())) {
				jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
				JobExecution lastExecution = jobRepository.getLastJobExecution(job.getJobName(), jobParameters);
				for (StepExecution execution : lastExecution.getStepExecutions()) {
					BatchStatus status = execution.getStatus();
					if (status.isRunning()) {
						execution.setStatus(BatchStatus.FAILED);
						execution.setExitStatus(ExitStatus.FAILED);
						execution.setEndTime(new Date());
						jobRepository.update(execution);
					}
				}
				lastExecution.setStatus(BatchStatus.FAILED);
				lastExecution.setExitStatus(ExitStatus.FAILED);
				lastExecution.setEndTime(new Date());
				jobRepository.update(lastExecution);
			} else {
				if(YesOrNoEnum.YES.equals(job.getIsMultiRun())) {
					jobInstanceId = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYYMMDDHHMISSSSS);
				}else {
					jobInstanceId = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYYMMDD);
				}
				jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
				logger.info("当前机器[ {} ]获取到分布式锁，开始执行任务[ {} ]", IPUtil.getLocalIP(), job.getJobName());
			}
			//更新job实例ID及状态
			job.setJobInstanceId(jobInstanceId);
			job.setStatus(BhJobStatusEnum.STARTING);
			bhJobMapper.updateById(job);
			try {
				//执行批量任务
				JobExecution result = jobLauncher.run((org.springframework.batch.core.Job) BeanUtil.getContext().getBean(job.getJobName()), jobParameters);
				logger.info("任务[ {} ]状态[ {} ]", job.getJobName(), null == result? "FAILED-NULL" : result.getStatus());
				if(null != result) {
					//更新任务状态
					job.convertStatus(result.getStatus());
					bhJobMapper.updateById(job);
				}
			} catch (JobInstanceAlreadyCompleteException e) {
				logger.warn("任务[ {} ]当天已经执行完成，禁止重复执行，执行日期：[ {} ]", job.getJobName(), jobInstanceId);
			} catch (BeansException | JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException e) {
				logger.error("批量执行异常", e);
				job.convertStatus(BatchStatus.FAILED);
				bhJobMapper.updateById(job);
			} finally {
				logger.info("删除任务[ {} ]分布式锁", job.getJobName());
				redisLockUtil.deleleJobLock(job.getJobName());
			}
		} else {
			logger.warn("当前机器[ {} ]未获取到分布式锁，本次任务[ {} ]跳过执行！", IPUtil.getLocalIP(), job.getJobName());
		}
	}
}