package com.spring.clould.batch.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CommonJobListener implements JobExecutionListener{
	
	private static Logger logger = LoggerFactory.getLogger(CommonJobListener.class);
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		logger.info("任务[{}]开始执行", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.info("任务[{}]结束执行", jobExecution.getJobInstance().getJobName());
	}

}
