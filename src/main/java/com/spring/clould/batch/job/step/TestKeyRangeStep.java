package com.spring.clould.batch.job.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.clould.batch.job.partitioner.KeyRangePartitioner;
import com.spring.clould.batch.job.step.base.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestKeyRangeTasklet;

@Configuration
public class TestKeyRangeStep extends BaseRemoteStep{

	@Bean
	public Step testKeyRangeMasterStep() {
		return this.managerStepBuilderFactory
				.get("testKeyRangeMasterStep")
				.partitioner("testKeyRangeWorkerStep", new KeyRangePartitioner<Integer>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadKeys", null))
				.gridSize(GRID_SIZE)
				.outputChannel(masterRequests())
				.build();
	}

	@Bean
	public Step testKeyRangeWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testKeyRangeWorkerStep")
				.inputChannel(workerRequests())
				.tasklet(testKeyRangeTasklet(null, null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testKeyRangeTasklet(@Value("#{stepExecutionContext[startId]}") final Integer startId,
			@Value("#{stepExecutionContext[endId]}") final Integer endId) {
		return new TestKeyRangeTasklet(startId, endId);
	}
}
