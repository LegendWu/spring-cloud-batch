package com.spring.clould.batch.job.base;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.mapper.BhJobMapper;
import com.spring.clould.batch.mapper.BhJobStepMapper;
import com.spring.clould.batch.util.BeanUtil;
import com.spring.clould.batch.util.ConvertUtil;
import com.spring.clould.batch.util.IPUtil;
import com.spring.clould.batch.util.RedisLockUtil;

/**
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@Component
public class QuartzJob implements Job {
	private Logger logger = LoggerFactory.getLogger(QuartzJob.class);

	@Autowired
	BhJobMapper bhJobMapper;

	@Autowired
	BhJobStepMapper bhJobStepMapper;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	RedisLockUtil redisLockUtil;

	@Override
	public void execute(JobExecutionContext executorContext) throws JobExecutionException {
		JobDataMap jobDataMap = executorContext.getMergedJobDataMap();
		BhJob job = ConvertUtil.convertToBhJob(jobDataMap);
		// 获取分布式锁
		boolean isLock = redisLockUtil.lock(job.getJobName());
		if (isLock) {
			logger.info("当前机器[{}]获取到分布式锁，开始执行调度任务[{}]", IPUtil.getLocalIP(), job.getJobName());
			JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();
			try {
				JobExecution result = jobLauncher.run(
						(org.springframework.batch.core.Job) BeanUtil.getContext().getBean(job.getJobName()),
						jobParameters);
				System.out.println(result.getStatus());
			} catch (BeansException | JobExecutionAlreadyRunningException | JobRestartException
					| JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
				logger.error("批量执行异常", e);
			} finally {
				logger.info("删除分布式锁", job.getJobName());
				redisLockUtil.delete(job.getJobName());
			}
		} else {
			logger.warn("当前机器[{}]未获取到分布式锁，本次任务[{}]跳过执行！", IPUtil.getLocalIP(), job.getJobName());
		}
	}
}