package com.spring.clould.batch.job.step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;

import com.spring.clould.batch.job.common.reader.CommonItemReader;
import com.spring.clould.batch.job.common.step.BaseRemoteStep;
import com.spring.clould.batch.job.writer.TestKeyStoreWriter;

/**
 * Description: 测试远程key存储类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.batch.job.step")
public class TestKeyStoreStep extends BaseRemoteStep<Integer> {
	
	@Bean("TestKeyStoreStep.masterChannel")
	public DirectChannel masterChannel() {
		return new DirectChannel();
	}
	
	@Bean("TestKeyStoreStep.workerChannel")
	public DirectChannel workerChannel() {
		return new DirectChannel();
	}
	
	@Bean("TestKeyStoreStep.outboundFlowMaster")
	public IntegrationFlow outboundFlowMaster() {
		return getMasterOutboundFlow();
	}
	
	@Bean("TestKeyStoreStep.inboundFlowMaster")
	public IntegrationFlow inboundFlowMaster() {
		return getMasterInboundFlow();
	}
	
	@Bean("TestKeyStoreStep.outboundFlowWorker")
	public IntegrationFlow outboundFlowWorker() {
		return getWorkerOutboundFlow();
	}
	
	@Bean("TestKeyStoreStep.inboundFlowWorker")
	public IntegrationFlow inboundFlowWorker() {
		return getWorkerInboundFlow();
	}
	
	@Bean("TestKeyStoreStep.masterStep")
	public Step masterStep() {
		return getKeyStoreMasterStep("com.spring.clould.batch.mapper.CatMapper.loadKeys", null);
	}

	@Bean("TestKeyStoreStep.workerStep")
	public Step workerStep() {
		return getWriterWorkerStep(reader(null), writer());
	}

	@StepScope
	@Bean("TestKeyStoreStep.reader")
	public ItemReader<Integer> reader(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new CommonItemReader(keyList);
	}
	
	@StepScope
	@Bean("TestKeyStoreStep.writer")
	public ItemWriter<Integer> writer() {
		return new TestKeyStoreWriter();
	}
	
	@Bean("TestKeyStoreStep.job")
    public Job job() {
         return getJob();
	}
}
