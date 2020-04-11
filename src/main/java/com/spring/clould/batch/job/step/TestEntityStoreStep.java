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

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.job.partitioner.KeyStorePartitioner;
import com.spring.clould.batch.job.step.base.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestEntityStoreTasklet;

/**
 * Description: 测试远程实体拆分类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
public class TestEntityStoreStep extends BaseRemoteStep{
	
	@Bean
	public DirectChannel masterA() {
		return new DirectChannel();
	}
	
	@Bean
	public DirectChannel workerA() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlowA(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(masterA())
				.handle(Jms.outboundAdapter(connectionFactory).destination("masterA")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowA(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("masterA"))
				.channel(masterA()).get();
	}
	
	@Bean
	public IntegrationFlow outboundFlowA1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(workerA())
				.handle(Jms.outboundAdapter(connectionFactory).destination("workerA")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlowA1(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("workerA"))
				.channel(workerA()).get();
	}
	
	@Bean
	public Step testEntityStoreMasterStep() {
		return this.managerStepBuilderFactory
				.get("testEntityStoreMasterStep")
				.partitioner("testEntityStoreWorkerStep", new KeyStorePartitioner<Cat>(sqlSessionFactory, "com.spring.clould.batch.mapper.CatMapper.loadAllCats", null))
				.gridSize(DEFAULT_GRID_SIZE)
				.outputChannel(masterA())
				.inputChannel(workerA())
				.listener(stepListener)
				.build();
	}

	@Bean
	public Step testEntityStoreWorkerStep() {
		return this.workerStepBuilderFactory
				.get("testEntityStoreWorkerStep")
				.inputChannel(masterA())
				.outputChannel(workerA())
				.tasklet(testEntityStoreTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet testEntityStoreTasklet(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new TestEntityStoreTasklet(keyList);
	}
}
