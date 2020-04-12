package com.spring.clould.batch.job.step;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.spring.clould.batch.job.common.partitioner.KeyRangePartitioner;
import com.spring.clould.batch.job.common.step.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestKeyRangeTasklet;

/**
 * Description: 测试远程key区间类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
public class TestKeyRangeStep extends BaseRemoteStep{
	
	@Bean
	public DirectChannel masterB() {
		return new DirectChannel();
	}
	
	@Bean
	public DirectChannel workerB() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlowB(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(masterB())
				.handle(Jms.outboundAdapter(connectionFactory).destination("masterB")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowB(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("masterB"))
				.channel(masterB()).get();
	}
	
	@Bean
	public IntegrationFlow outboundFlowB1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(workerB())
				.handle(Jms.outboundAdapter(connectionFactory).destination("workerB")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowB1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("workerB"))
				.channel(workerB()).get();
	}

	@Bean
	public Step testKeyRangeMasterStep() {
		return this.managerStepBuilderFactory
				.get("testKeyRangeMasterStep")
				.partitioner("testKeyRangeWorkerStep", new KeyRangePartitioner<Integer>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadKeys", null))
				.gridSize(DEFAULT_GRID_SIZE)
				.outputChannel(masterB())
				.inputChannel(workerB())
				.listener(stepListener)
				.build();
	}

	@Bean
	public Step testKeyRangeWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testKeyRangeWorkerStep")
				.inputChannel(masterB())
				.outputChannel(workerB())
				.tasklet(testKeyRangeTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testKeyRangeTasklet(@Value("#{stepExecutionContext[keyMap]}") final String keyMap) {
		return new TestKeyRangeTasklet(keyMap);
	}
	
	@Bean
    public Job testKeyRangeJob() {
         return jobBuilderFactory.get("testKeyRangeJob")
                 .start(testKeyRangeMasterStep())
                 .listener(jobListener)
                 .build();
	}
}
