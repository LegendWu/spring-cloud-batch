package com.spring.clould.batch;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.test.context.junit4.SpringRunner;

import com.spring.clould.batch.config.RedisConfig;
import com.spring.clould.batch.job.step.TestKeyStoreStep;
import com.spring.clould.batch.util.DateUtil;
import com.spring.clould.batch.util.RedisLockUtil;
import com.spring.clould.batch.util.StepBeanUtil;

/**
 * Description: 测试启动类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(value = {RedisConfig.class})
public class BatchAppTests {
	
	@Autowired
	IntegrationFlow outboundFlowC;

	@Autowired
	RedisLockUtil redisLockUtil;
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	StepBeanUtil stepBeanUtil;
	
	@Test
	public void deleteRedisLock() {
		redisLockUtil.deleleJobLock("testJob");
	}
	
	@Test
	public void runJob() throws SchedulerException, BeansException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		String jobName=StepBeanUtil.getMasterStepName(TestKeyStoreStep.class);
		redisLockUtil.deleleJobLock(jobName);
		String jobInstanceId = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYYMMDDHHMISSSSS);
		JobParameters jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
		JobExecution result = jobLauncher.run(stepBeanUtil.getJob(TestKeyStoreStep.class), jobParameters);
		System.out.println(result.getStatus());
	}


}
