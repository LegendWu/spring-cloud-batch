package com.spring.clould.batch.job.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Description: step监听器
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Component
public class CommonStepListener implements StepExecutionListener{
	
	private static Logger logger = LoggerFactory.getLogger(CommonStepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.info("=========步骤[{}]开始执行=================", stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		long start = stepExecution.getStartTime().getTime();
		logger.info("=========步骤[{}]结束执行================= ### 耗时：{} ms", stepExecution.getStepName(), System.currentTimeMillis()-start);
		return null;
	}

}
