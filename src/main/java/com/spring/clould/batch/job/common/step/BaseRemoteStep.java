package com.spring.clould.batch.job.common.step;

import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.spring.clould.batch.job.common.listener.CommonJobListener;
import com.spring.clould.batch.job.common.listener.CommonStepListener;
import com.spring.clould.batch.job.common.partitioner.KeyRangePartitioner;
import com.spring.clould.batch.job.common.partitioner.KeyStorePartitioner;
import com.spring.clould.batch.util.StepBeanUtil;

/**
 * Description: 基础远程步骤
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class BaseRemoteStep<T> {

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
	
	@Autowired
	protected ActiveMQConnectionFactory connectionFactory;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private StepBeanUtil stepBeanUtil;
	
	private int gridSize;
	
	private int chunk;
	
	public IntegrationFlow getMasterOutboundFlow() {
		return IntegrationFlows.from(stepBeanUtil.getMasterChannel(getClass()))
				.handle(Jms.outboundAdapter(connectionFactory).destination(StepBeanUtil.getMasterChannelName(getClass()))).get();
	}
	
	public IntegrationFlow getMasterInboundFlow() {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination(StepBeanUtil.getMasterChannelName(getClass())))
				.channel(stepBeanUtil.getMasterChannel(getClass())).get();
	}
	
	public IntegrationFlow getWorkerOutboundFlow() {
		return IntegrationFlows.from(stepBeanUtil.getWorkerChannel(getClass()))
				.handle(Jms.outboundAdapter(connectionFactory).destination(StepBeanUtil.getWorkerChannelName(getClass()))).get();
	}
	
	public IntegrationFlow getWorkerInboundFlow() {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination(StepBeanUtil.getWorkerChannelName(getClass())))
				.channel(stepBeanUtil.getWorkerChannel(getClass())).get();
	}
	
	public Step getKeyStoreMasterStep(String queryId, Map<String, Object> param) {
		return this.managerStepBuilderFactory
				.get(StepBeanUtil.getMasterStepName(getClass()))
				.partitioner(StepBeanUtil.getWorkerStepName(getClass()), new KeyStorePartitioner<T>(sqlSessionFactory, queryId, param))
				.gridSize(gridSize)
				.outputChannel(stepBeanUtil.getMasterChannel(getClass()))
				.inputChannel(stepBeanUtil.getWorkerChannel(getClass()))
				.listener(stepListener)
				.build();
	}
	
	public Step getKeyRangeMasterStep(String queryId, Map<String, Object> param) {
		return this.managerStepBuilderFactory
				.get(StepBeanUtil.getMasterStepName(getClass()))
				.partitioner(StepBeanUtil.getWorkerStepName(getClass()), new KeyRangePartitioner<T>(sqlSessionFactory, queryId, param))
				.gridSize(gridSize)
				.outputChannel(stepBeanUtil.getMasterChannel(getClass()))
				.inputChannel(stepBeanUtil.getWorkerChannel(getClass()))
				.listener(stepListener)
				.build();
	}
	
	public Step getTaskletWorkerStep(Tasklet tasklet) {
		return this.workerStepBuilderFactory
				.get(StepBeanUtil.getWorkerStepName(getClass()))
				.inputChannel(stepBeanUtil.getMasterChannel(getClass()))
				.outputChannel(stepBeanUtil.getWorkerChannel(getClass()))
				.tasklet(tasklet)
				.build();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Step getWriterWorkerStep(ItemReader<Integer> reader, ItemWriter writer) {
		return this.workerStepBuilderFactory
				.get(StepBeanUtil.getWorkerStepName(getClass()))
				.inputChannel(stepBeanUtil.getMasterChannel(getClass()))
				.outputChannel(stepBeanUtil.getWorkerChannel(getClass()))
				.chunk(chunk)
				.reader(reader)
				.writer(writer)
				.taskExecutor(taskExecutor)
				.build();
	}
	
	public Job getJob() {
		return jobBuilderFactory.get(StepBeanUtil.getJobName(getClass()))
                .start(stepBeanUtil.getMasterStep(getClass()))
                .listener(jobListener)
                .build();
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int getChunk() {
		return chunk;
	}

	public void setChunk(int chunk) {
		this.chunk = chunk;
	}
	
}
