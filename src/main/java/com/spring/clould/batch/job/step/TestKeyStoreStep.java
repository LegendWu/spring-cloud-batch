package com.spring.clould.batch.job.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.clould.batch.job.partitioner.KeyStorePartitioner;
import com.spring.clould.batch.job.step.base.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestKeyStoreTasklet;

/**
 * Description: 测试远程key存储类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
public class TestKeyStoreStep extends BaseRemoteStep{

	@Bean
	public Step testKeyStoreMasterStep() {
		return this.managerStepBuilderFactory
				.get("testKeyStoreMasterStep")
				.partitioner("testKeyStoreWorkerStep", new KeyStorePartitioner<Integer>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadKeys", null))
				.gridSize(DEFAULT_GRID_SIZE)
				.outputChannel(masterRequests())
				.listener(stepListener)
				.build();
	}

	@Bean
	public Step testKeyStoreWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testKeyStoreWorkerStep")
				.inputChannel(workerRequests())
				.tasklet(testKeyStoreTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testKeyStoreTasklet(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new TestKeyStoreTasklet(keyList);
	}
}
