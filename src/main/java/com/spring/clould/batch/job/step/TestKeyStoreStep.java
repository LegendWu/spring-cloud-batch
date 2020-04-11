package com.spring.clould.batch.job.step;

import org.apache.activemq.ActiveMQConnectionFactory;
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
	public DirectChannel masterC() {
		return new DirectChannel();
	}
	
	@Bean
	public DirectChannel workerC() {
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow outboundFlowC(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(masterC())
				.handle(Jms.outboundAdapter(connectionFactory).destination("masterC")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowC1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("workerC"))
				.channel(workerC()).get();
	}
	
	@Bean
	public IntegrationFlow outboundFlowC1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(workerC())
				.handle(Jms.outboundAdapter(connectionFactory).destination("workerC")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowC(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("masterC"))
				.channel(masterC()).get();
	}

	@Bean
	public Step testKeyStoreMasterStep() {
		return this.managerStepBuilderFactory
				.get("testKeyStoreMasterStep")
				.partitioner("testKeyStoreWorkerStep", new KeyStorePartitioner<Integer>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadKeys", null))
				.gridSize(DEFAULT_GRID_SIZE)
				.outputChannel(masterC())
				.inputChannel(workerC())
				.listener(stepListener)
				.build();
	}

	@Bean
	public Step testKeyStoreWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testKeyStoreWorkerStep")
				.inputChannel(masterC())
				.outputChannel(workerC())
				.tasklet(testKeyStoreTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testKeyStoreTasklet(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new TestKeyStoreTasklet(keyList);
	}
}
