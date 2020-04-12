package com.spring.clould.batch.job.step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.job.common.step.BaseRemoteStep;
import com.spring.clould.batch.job.tasklet.TestEntityStoreTasklet;

/**
 * Description: 测试远程实体拆分类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.batch.job.step")
public class TestEntityStoreStep extends BaseRemoteStep<Cat>{
	
	@Bean("TestEntityStoreStep.masterChannel")
	public DirectChannel masterChannel() {
		return new DirectChannel();
	}
	
	@Bean("TestEntityStoreStep.workerChannel")
	public DirectChannel workerChannel() {
		return new DirectChannel();
	}

	@Bean("TestEntityStoreStep.outboundFlowMaster")
	public IntegrationFlow outboundFlowMaster() {
		return getMasterOutboundFlow();
	}
	
	@Bean("TestEntityStoreStep.inboundFlowMaster")
	public IntegrationFlow inboundFlowMaster() {
		return getMasterInboundFlow();
	}
	
	@Bean("TestEntityStoreStep.outboundFlowWorker")
	public IntegrationFlow outboundFlowWorker() {
		return getWorkerOutboundFlow();
	}
	
	@Bean("TestEntityStoreStep.inboundFlowWorker")
	public IntegrationFlow inboundFlowWorker() {
		return getWorkerInboundFlow();
	}
	
	@Bean("TestEntityStoreStep.masterStep")
	public Step masterStep() {
		return getKeyStoreMasterStep("com.spring.clould.batch.mapper.CatMapper.loadAllCats", null);
	}

	@Bean("TestEntityStoreStep.workerStep")
	public Step workerStep() {
		return getTaskletWorkerStep(tasklet(null));
	}

	@StepScope
	@Bean("TestEntityStoreStep.tasklet")
	public Tasklet tasklet(@Value("#{stepExecutionContext[keyList]}") final String keyList) {
		return new TestEntityStoreTasklet(keyList);
	}
	
	@Bean("TestEntityStoreStep.job")
    public Job job() {
         return getJob();
	}
}
