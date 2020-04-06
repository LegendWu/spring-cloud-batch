package com.spring.clould.batch.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.spring.clould.batch.entity.BatchJob;
import com.spring.clould.batch.entity.enums.BatchJobStatusEnum;
import com.spring.clould.batch.mapper.BatchJobMapper;
import com.spring.clould.batch.util.ConvertUtil;
import com.spring.clould.batch.util.CronUtil;
import com.spring.clould.batch.util.RedisLockUtil;

/**
 * Description: 定时任务配置
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
public class QuartzConfig {
	
	private static Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

	@Autowired
	private SchedulerFactoryBean factory;

	@Autowired
	private BatchJobMapper batchJobMapper;
	
	@Autowired
	RedisLockUtil redisLockUtil;
	
	@Autowired
	JobRepository jobRepository;
	
    //配置JobFactory
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
    /**
     * SchedulerFactoryBean这个类的真正作用提供了对org.quartz.Scheduler的创建与配置，并且会管理它的生命周期与Spring同步。
     * org.quartz.Scheduler: 调度器。所有的调度都是由它控制。
     * @param dataSource 为SchedulerFactory配置数据源
     * @param jobFactory 为SchedulerFactory配置JobFactory
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //可选,QuartzScheduler启动时更新己存在的Job,这样就不用每次修改targetObject后删除qrtz_job_details表对应记录
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true); //设置自行启动
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }
    //从quartz.properties文件中读取Quartz配置属性
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
    //配置JobFactory,为quartz作业添加自动连接支持
    public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;
        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }
        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
    
    /**
	 * 任务更新时刷新任务
	 * 
	 * @throws SchedulerException
	 */
	@Scheduled(fixedRate=15000)
	public void jobRefresh() throws SchedulerException {
		boolean isLock = redisLockUtil.lockJobRefresh();
		if(isLock) {
			refreshAllJobs();
			redisLockUtil.deleleJobRefreshLock();
		}
	}

	/**
	 * 重新启动所有的job
	 */
	private void refreshAllJobs() throws SchedulerException {
		Scheduler scheduler = factory.getScheduler();
		if(!scheduler.isStarted()) {
			scheduler.start();
		}
		List<BatchJob> jobs = batchJobMapper.selectList(null);
		List<String> allJobs = new ArrayList<String>();
		for (BatchJob job : jobs) {
			allJobs.add(job.getJobName());
			JobKey jobKey = getJobKey(job);
			JobDataMap newMap = ConvertUtil.convertToJobDataMap(job);
			JobDetail jobDetail = geJobDetail(jobKey, job.getDescription(), newMap);
			switch (job.getStatus()) {
			case STARTED:
			case COMPLETED:
				if (scheduler.checkExists(jobKey)) {
					JobDataMap lastMap = scheduler.getJobDetail(jobKey).getJobDataMap();
					// 如果任务更新，则刷新调度任务
					if (!newMap.get("cron").equals(lastMap.get("cron"))
							|| !newMap.get("status").toString().equals(lastMap.get("status").toString())) {
						scheduler.deleteJob(jobKey);
						scheduler.scheduleJob(jobDetail, getTrigger(job));
						logger.info("刷新调度任务-批量[{}]状态为[{}]，原调度时间[{}]，当前调度时间[{}]", job.getJobName(), job.getStatus(), lastMap.get("cron"), newMap.get("cron"));
					}
				} else {
					scheduler.scheduleJob(jobDetail, getTrigger(job));
					logger.info("新增调度任务-批量[{}]状态为[{}]，调度时间[{}]", job.getJobName(), job.getStatus(), job.getCron());
				}
				//如果当前CRON和模板配置不一致则更新
				if(!job.getCron().equals(job.getCronTemplate())) {
					batchJobMapper.updateJobCronByTemplate(job);
					scheduler.deleteJob(jobKey);
					scheduler.scheduleJob(jobDetail, getTrigger(job));
					logger.info("还原调度任务-批量[{}]状态为[{}]，模板调度时间[{}]，当前调度时间[{}]，更新后当前调度时间[{}]", job.getJobName(), job.getStatus(), job.getCronTemplate(), job.getCron(), job.getCronTemplate());
				}
				break;
			case FAILED:
				if(redisLockUtil.hasJobLock(job.getJobName())) {
					//判断是否批量异常，如果是则删除分布式锁并且更新调度时间
					JobParameters jobParameters = new JobParametersBuilder().addString(job.getJobInstanceId(), "datetime").toJobParameters();
					JobExecution lastExecution = jobRepository.getLastJobExecution(job.getJobName(), jobParameters);
					if(lastExecution != null) {
						if(lastExecution.getStatus().equals(BatchStatus.FAILED) 
								&& lastExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
							int startedCount = 0;
							boolean noNeedUpdate = false;
							for (StepExecution execution : lastExecution.getStepExecutions()) {
								if(execution.getStatus().equals(BatchStatus.STARTED) || execution.getStatus().equals(BatchStatus.STARTING)) {
									startedCount++;
								}
								Date endTime = execution.getEndTime();
								//如果任一执行步骤的结束时间比当前时间小1分钟，则无需更新任务状态
								if(null != endTime && System.currentTimeMillis()-endTime.getTime() < 1*60*1000) {
									noNeedUpdate = true;
								}
							}
							if(startedCount == 0 && !noNeedUpdate) {
								logger.warn("任务 [{}] 已经1分钟没有更新，系统判断为异常，尝试修改状态自动续跑.....", job.getJobName());
								//更新任务表（调度时间改为当前时间加一分钟）
								job.setCron(CronUtil.createCronByCurrentTimeAddMillis(1));
								job.setStatus(BatchJobStatusEnum.FAILED);
								batchJobMapper.updateById(job);
								//重新调度任务
								scheduler.deleteJob(jobKey);
								scheduler.scheduleJob(jobDetail, getTrigger(job));
								logger.info("自动续跑，刷新调度任务-批量[{}]状态为[{}]，调度时间[{}]", job.getJobName(), job.getStatus(), job.getCron());
								//删除分布式锁
								redisLockUtil.deleleJobLock(job.getJobName());
								logger.info("自动续跑，删除任务[{}]分布式锁", job.getJobName());
							}
						}
					}
				}
				break;
			case STARTING:
				if(redisLockUtil.hasJobLock(job.getJobName())) {
					//判断是否批量异常，如果是则删除分布式锁并且更新调度时间
					JobParameters jobParameters = new JobParametersBuilder().addString(job.getJobInstanceId(), "datetime").toJobParameters();
					JobExecution lastExecution = jobRepository.getLastJobExecution(job.getJobName(), jobParameters);
					if(lastExecution != null) {
						if(lastExecution.getStatus().equals(BatchStatus.STARTED) 
								&& lastExecution.getExitStatus().equals(ExitStatus.UNKNOWN)) {
							int startedCount = 0;
							boolean noNeedUpdate = false;
							StepExecution needUpdateExecution = null;
							for (StepExecution execution : lastExecution.getStepExecutions()) {
								if(execution.getStatus().equals(BatchStatus.STARTED)
										&& execution.getExitStatus().equals(ExitStatus.EXECUTING)) {
									startedCount++;
									needUpdateExecution = execution;
								}
								Date endTime = execution.getEndTime();
								//如果任一执行步骤的结束时间比当前时间小1分钟，则无需更新任务状态
								if(null != endTime && System.currentTimeMillis()-endTime.getTime() < 1*60*1000) {
									noNeedUpdate = true;
								}
							}
							if(startedCount == 1 && null != needUpdateExecution && !noNeedUpdate) {
								logger.warn("任务 [{}] 已经1分钟没有更新，系统判断为异常，尝试修改状态自动续跑.....", job.getJobName());
								//更新step状态
								batchJobMapper.updateStepExecutionFailed(needUpdateExecution.getId());
								//更新job状态
								batchJobMapper.updateJobExecutionFailed(lastExecution.getId());
								//更新任务表（调度时间改为当前时间加一分钟）
								job.setCron(CronUtil.createCronByCurrentTimeAddMillis(1));
								job.setStatus(BatchJobStatusEnum.FAILED);
								batchJobMapper.updateById(job);
								//重新调度任务
								scheduler.deleteJob(jobKey);
								scheduler.scheduleJob(jobDetail, getTrigger(job));
								logger.info("自动续跑，刷新调度任务-批量[{}]状态为[{}]，调度时间[{}]", job.getJobName(), job.getStatus(), job.getCron());
								//删除分布式锁
								redisLockUtil.deleleJobLock(job.getJobName());
								logger.info("自动续跑，删除任务[{}]分布式锁", job.getJobName());
							}
						}
					}
				}
				break;
			case UNKNOWN:
//				if(!redisLockUtil.hasJobLock(job.getJobName())) {
					//判断是否批量异常，如果是则删除分布式锁并且更新调度时间
					JobParameters jobParameters = new JobParametersBuilder().addString(job.getJobInstanceId(), "datetime").toJobParameters();
					JobExecution lastExecution = jobRepository.getLastJobExecution(job.getJobName(), jobParameters);
					if(lastExecution != null) {
						if(lastExecution.getStatus().equals(BatchStatus.UNKNOWN) 
								&& lastExecution.getExitStatus().getExitCode().equals(ExitStatus.UNKNOWN.getExitCode())) {
							int startedCount = 0;
							boolean noNeedUpdate = false;
							for (StepExecution execution : lastExecution.getStepExecutions()) {
								if(execution.getStatus().equals(BatchStatus.STARTED) || execution.getStatus().equals(BatchStatus.STARTING)) {
									startedCount++;
								}
								Date endTime = execution.getEndTime();
								//如果任一执行步骤的结束时间比当前时间小1分钟，则无需更新任务状态
								if(null != endTime && System.currentTimeMillis()-endTime.getTime() < 1*60*1000) {
									noNeedUpdate = true;
								}
							}
							if(startedCount == 0 && !noNeedUpdate) {
								logger.warn("任务 [{}] 已经1分钟没有更新，系统判断为异常，尝试修改状态自动续跑.....", job.getJobName());
								//更新任务表（调度时间改为当前时间加一分钟）
								job.setCron(CronUtil.createCronByCurrentTimeAddMillis(1));
								job.setStatus(BatchJobStatusEnum.FAILED);
								batchJobMapper.updateById(job);
								//更新job状态
								batchJobMapper.updateJobExecutionFailed(lastExecution.getJobId());
								//重新调度任务
								scheduler.deleteJob(jobKey);
								scheduler.scheduleJob(jobDetail, getTrigger(job));
								logger.info("自动续跑，刷新调度任务-批量[{}]状态为[{}]，调度时间[{}]", job.getJobName(), job.getStatus(), job.getCron());
								//删除分布式锁
								redisLockUtil.deleleJobLock(job.getJobName());
								logger.info("自动续跑，删除任务[{}]分布式锁", job.getJobName());
							}
						}
					}
//				}
				break;
			case STOPPING:
			case STOPPED:
			case ABANDONED:
			default:
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
					logger.info("删除调度任务-批量[{}]状态为[{}]", job.getJobName(), job.getStatus());
				}
				break;
			}
		}
		// 将不存在于任务表的定时任务删除
		Set<JobKey> set = scheduler.getJobKeys(GroupMatcher.anyGroup());
		for (JobKey jobKey : set) {
			if (!allJobs.contains(jobKey.getName())) {
				scheduler.deleteJob(jobKey);
			}
		}
	}

	/**
	 * 获取JobDetail,JobDetail是任务的定义,而Job是任务的执行逻辑,JobDetail里会引用一个Job Class来定义
	 * 
	 * @param jobKey
	 * @param description
	 * @param map
	 * @return
	 */
	public JobDetail geJobDetail(JobKey jobKey, String description, JobDataMap map) {
		return JobBuilder.newJob(QuartzJobConfig.class).withIdentity(jobKey).withDescription(description).setJobData(map)
				.storeDurably().build();
	}

	/**
	 * 获取Trigger (Job的触发器,执行规则)
	 * 
	 * @param job
	 * @return
	 */
	public Trigger getTrigger(BatchJob job) {
		return TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
				.withSchedule(CronScheduleBuilder.cronSchedule(job.getCron())).build();
	}

	/**
	 * 获取JobKey,包含Name和Group
	 * 
	 * @param job
	 * @return
	 */
	public JobKey getJobKey(BatchJob job) {
		return JobKey.jobKey(job.getJobName(), job.getJobGroup());
	}
}
