package com.spring.clould.batch.config;

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

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.job.base.QuartzJob;
import com.spring.clould.batch.mapper.BhJobMapper;
import com.spring.clould.batch.util.CompareUtil;
import com.spring.clould.batch.util.ConvertUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Configuration
public class QuartzConfig {
	
	private static Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

	@Autowired
	private SchedulerFactoryBean factory;

	@Autowired
	private BhJobMapper bhJobMapper;
	
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
	@Scheduled(fixedRate=30000)
	public void jobRefresh() throws SchedulerException {
		reStartAllJobs();
	}

	/**
	 * 重新启动所有的job
	 */
	private void reStartAllJobs() throws SchedulerException {
		Scheduler scheduler = factory.getScheduler();
		if(!scheduler.isStarted()) {
			scheduler.start();
		}
		List<BhJob> jobs = bhJobMapper.selectList(null);
		List<String> allJobs = new ArrayList<String>();
		for (BhJob job : jobs) {
			allJobs.add(job.getJobName());
			JobKey jobKey = getJobKey(job);
			switch (job.getStatus()) {
			case RUNABLE:
			case COMPLETED:
				JobDataMap newMap = ConvertUtil.convertToJobDataMap(job);
				JobDetail jobDetail = geJobDetail(jobKey, job.getDescription(), newMap);
				if (scheduler.checkExists(jobKey)) {
					JobDataMap lastMap = scheduler.getJobDetail(jobKey).getJobDataMap();
					List<String> excludeFields = new ArrayList<String>();
					excludeFields.add("status");
					// 如果任务更新，则刷新调度任务
					if (CompareUtil.isMapDifferent(newMap, lastMap, excludeFields)) {
						scheduler.deleteJob(jobKey);
						scheduler.scheduleJob(jobDetail, getTrigger(job));
						logger.info("批量[{}]状态为[{}]，刷新调度任务...", job.getJobName(), job.getStatus());
					}
				} else {
					scheduler.scheduleJob(jobDetail, getTrigger(job));
					logger.info("批量[{}]状态为[{}]，新增调度任务...", job.getJobName(), job.getStatus());
				}
				break;
			case RUNNING:
				break;
			case WAITING:
				scheduler.deleteJob(jobKey);
				logger.info("批量[{}]状态为[{}]，任务正在等待执行，需修改状态为RUNABLE才可执行", job.getJobName(), job.getStatus());
				break;
			case STOPPED:
			case FAILED:
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
					logger.info("批量[{}]状态为[{}]，删除调度任务...", job.getJobName(), job.getStatus());
				}
				break;
			default:
				scheduler.deleteJob(jobKey);
				logger.info("批量[{}]状态为[{}]，删除调度任务...", job.getJobName(), job.getStatus());
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
		return JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).withDescription(description).setJobData(map)
				.storeDurably().build();
	}

	/**
	 * 获取Trigger (Job的触发器,执行规则)
	 * 
	 * @param job
	 * @return
	 */
	public Trigger getTrigger(BhJob job) {
		return TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
				.withSchedule(CronScheduleBuilder.cronSchedule(job.getCron())).build();
	}

	/**
	 * 获取JobKey,包含Name和Group
	 * 
	 * @param job
	 * @return
	 */
	public JobKey getJobKey(BhJob job) {
		return JobKey.jobKey(job.getJobName(), job.getJobGroup());
	}
}
