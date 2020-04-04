package com.spring.clould.batch.config;

import java.io.IOException;
import java.util.ArrayList;
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
import com.spring.clould.batch.mapper.BatchJobMapper;
import com.spring.clould.batch.util.ConvertUtil;
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
	@Scheduled(fixedRate=10000)
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
					if (!newMap.get("cron").equals(lastMap.get("cron"))) {
						scheduler.deleteJob(jobKey);
						scheduler.scheduleJob(jobDetail, getTrigger(job));
						logger.info("刷新调度任务-批量[ {} ]状态为[ {} ]，原调度时间[ {} ]，当前调度时间[ {} ]", job.getJobName(), job.getStatus(), lastMap.get("cron"), newMap.get("cron"));
					}
				} else {
					scheduler.scheduleJob(jobDetail, getTrigger(job));
					logger.info("新增调度任务-批量[ {} ]状态为[ {} ]，调度时间[ {} ]", job.getJobName(), job.getStatus(), job.getCron());
				}
				break;
			case FAILED:
			case STARTING:
				if(!redisLockUtil.hasJobLock(job.getJobName())) {
					scheduler.deleteJob(jobKey);
					scheduler.scheduleJob(jobDetail, getTrigger(job));
					logger.info("刷新调度任务-批量[ {} ]状态为[ {} ]，调度时间[ {} ]", job.getJobName(), job.getStatus(), job.getCron());
				}
				break;
			case STOPPING:
			case STOPPED:
			case ABANDONED:
			default:
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
					logger.info("删除调度任务-批量[ {} ]状态为[ {} ]", job.getJobName(), job.getStatus());
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
