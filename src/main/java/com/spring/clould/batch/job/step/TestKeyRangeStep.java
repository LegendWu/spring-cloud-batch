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
@ConfigurationProperties(prefix = "spring.batch.job.step")
public class TestKeyRangeStep extends BaseRemoteStep<Integer>{

	@Bean("TestKeyRangeStep.masterChannel")
	public DirectChannel masterChannel() {
		return new DirectChannel();
	}
	
	@Bean("TestKeyRangeStep.workerChannel")
	public DirectChannel workerChannel() {
		return new DirectChannel();
	}
	
	@Bean("TestKeyRangeStep.outboundFlowMaster")
	public IntegrationFlow outboundFlowMaster() {
		return getMasterOutboundFlow();
	}
	
	@Bean("TestKeyRangeStep.inboundFlowMaster")
	public IntegrationFlow inboundFlowMaster() {
		return getMasterInboundFlow();
	}
	
	@Bean("TestKeyRangeStep.outboundFlowWorker")
	public IntegrationFlow outboundFlowWorker() {
		return getWorkerOutboundFlow();
	}
	
	@Bean("TestKeyRangeStep.inboundFlowWorker")
	public IntegrationFlow inboundFlowWorker() {
		return getWorkerInboundFlow();
	}
	
	@Bean("TestKeyRangeStep.masterStep")
	public Step masterStep() {
		return getKeyRangeMasterStep("com.spring.clould.batch.mapper.CatMapper.loadKeys", null);
	}

	@Bean("TestKeyRangeStep.workerStep")
	public Step workerStep() {
		return getTaskletWorkerStep(tasklet(null));
	}

	@StepScope
	@Bean("TestKeyRangeStep.tasklet")
	public Tasklet tasklet(@Value("#{stepExecutionContext[keyMap]}") final String keyMap) {
		return new TestKeyRangeTasklet(keyMap);
	}
	
	@Bean("TestKeyRangeStep.job")
    public Job job() {
         return getJob();
	}
}
