package com.spring.clould.batch.job.common.step;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.clould.batch.job.common.listener.CommonJobListener;
import com.spring.clould.batch.job.common.listener.CommonStepListener;

/**
 * Description: 基础远程步骤
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class BaseRemoteStep {

	protected static final int DEFAULT_GRID_SIZE = 10000;

	@Autowired
	protected SqlSessionFactory sqlSessionFactory;

	@Autowired
	protected RemotePartitioningMasterStepBuilderFactory managerStepBuilderFactory;

	@Autowired
	protected RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
	
	@Autowired
	protected CommonStepListener stepListener;
	
	@Autowired
	protected CommonJobListener jobListener;
	
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
	
}
