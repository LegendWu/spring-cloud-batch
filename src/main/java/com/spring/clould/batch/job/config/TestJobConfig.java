package com.spring.clould.batch.job.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.clould.batch.job.listener.CommonJobListener;

/**
 * Description: 远程分片任务配置
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class TestJobConfig {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	CommonJobListener listener;
	
	@Autowired
	Step testKeyRangeMasterStep;
	
	@Autowired
	Step testKeyStoreMasterStep;
	
	@Autowired
	Step testEntityStoreMasterStep;
	
	@Bean
    public Job testJob() {
         return jobBuilderFactory.get("testJob")
                 .start(testKeyRangeMasterStep)
                 .next(testKeyStoreMasterStep)
                 .next(testEntityStoreMasterStep)
                 .listener(listener)
                 .build();
    }
}
