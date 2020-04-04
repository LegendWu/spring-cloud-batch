package com.spring.clould.batch.job.step.base;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.spring.clould.batch.job.listener.CommonStepListener;

/**
 * Description: 基础远程步骤
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class BaseRemoteStep {

	protected static final int DEFAULT_GRID_SIZE = 1000;

	@Autowired
	protected SqlSessionFactory sqlSessionFactory;

	@Autowired
	protected RemotePartitioningMasterStepBuilderFactory managerStepBuilderFactory;

	@Autowired
	protected RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
	
	@Autowired
	protected CommonStepListener stepListener;

	@Bean
	public DirectChannel masterRequests() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel workerRequests() {
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(masterRequests())
				.handle(Jms.outboundAdapter(connectionFactory).destination("masterRequests")).get();
	}
	
	@Bean
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("masterRequests"))
				.channel(workerRequests()).get();
	}

}
