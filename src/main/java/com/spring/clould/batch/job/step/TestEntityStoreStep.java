package com.spring.clould.batch.job.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.job.partitioner.KeyStorePartitioner;
import com.spring.clould.batch.job.step.base.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestEntityStoreTasklet;

@Configuration
public class TestEntityStoreStep extends BaseRemoteStep{

	@Bean
	public Step testEntityStoreMasterStep() {
		return this.managerStepBuilderFactory
				.get("testEntityStoreMasterStep")
				.partitioner("testEntityStoreWorkerStep", new KeyStorePartitioner<Cat>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadAllCats", null))
				.gridSize(DEFAULT_GRID_SIZE)
				.outputChannel(masterRequests())
				.build();
	}

	@Bean
	public Step testEntityStoreWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testEntityStoreWorkerStep")
				.inputChannel(workerRequests())
				.tasklet(testEntityStoreTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testEntityStoreTasklet(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new TestEntityStoreTasklet(keyList);
	}
}
