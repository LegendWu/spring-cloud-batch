package com.spring.clould.batch.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Description: job监听器
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Component
public class CommonJobListener implements JobExecutionListener{
	
	private static Logger logger = LoggerFactory.getLogger(CommonJobListener.class);
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		logger.info("=========任务 [ {} ] 开始执行=================", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		long start = jobExecution.getStartTime().getTime();
		logger.info("=========任务 [ {} ] 结束执行================= ### 耗时：{} ms", jobExecution.getJobInstance().getJobName(), System.currentTimeMillis()-start);
	}

}
