package com.spring.clould.batch.listener;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RestController;

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.entity.enums.BhJobStatusEnum;
import com.spring.clould.batch.job.base.QuartzJob;
import com.spring.clould.batch.mapper.BhJobMapper;

@RestController
public class JobListener {
	private static Logger logger = LoggerFactory.getLogger(JobListener.class);

	@Autowired
	private SchedulerFactoryBean factory;

	@Autowired
	private BhJobMapper bhJobMapper;

	public static List<BhJob> jobs;

	// 初始化启动所有的Job
	@PostConstruct
	public void initialize() {
		try {
			reStartAllJobs();
			logger.info("INIT SUCCESS");
		} catch (SchedulerException e) {
			logger.info("INIT EXCEPTION : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 任务更新时刷新任务
	 * 
	 * @throws SchedulerException
	 */
	@Scheduled(cron = "0/15 * * * * ?")
	public void jobRefresh() throws SchedulerException {
//		List<BhJob> currentJobs = bhJobMapper.selectList(new QueryWrapper<BhJob>());
		// 判断当前jobs和前一次的jobs是否有修改

		reStartAllJobs();
	}

	/**
	 * 重新启动所有的job
	 */
	private void reStartAllJobs() throws SchedulerException {
		Scheduler scheduler = factory.getScheduler();
		Set<JobKey> set = scheduler.getJobKeys(GroupMatcher.anyGroup());
		for (JobKey jobKey : set) {
			scheduler.deleteJob(jobKey);
		}
		jobs = bhJobMapper.selectList(null);
		for (BhJob job : jobs) {
			JobDataMap map = new JobDataMap();
			map.put("id", job.getId());
			map.put("jobName", job.getJobName());
			JobKey jobKey = getJobKey(job);
			JobDetail jobDetail = geJobDetail(jobKey, job.getDescription(), map);
			if (job.getStatus() == BhJobStatusEnum.RUNABLE) {
				scheduler.scheduleJob(jobDetail, getTrigger(job));
			} else {
				logger.info("当前批量[{}]状态为[{}]，本次跳过", job.getJobName(), job.getStatus());
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
