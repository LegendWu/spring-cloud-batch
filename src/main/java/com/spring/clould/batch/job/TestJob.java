package com.spring.clould.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.job.listener.TestListener;
import com.spring.clould.batch.job.processor.TestProcessor;
import com.spring.clould.batch.job.reader.TestReader;
import com.spring.clould.batch.job.writer.TestWriter;

@Configuration
@EnableBatchProcessing
public class TestJob {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	TestReader reader;
	
	@Autowired
	TestProcessor processor;
	
	@Autowired
	TestWriter writer;
	
	@Autowired
	TestListener listener;
	
	@Bean("test_job_1")
	public Job job1(ApplicationContext ac) {
		this.getClass().getAnnotations();
		return jobBuilderFactory
				.get("test_job_1")
				.listener(listener)
				.start(step(ac))
				.build();
	}
	
	@Bean("test_job_2")
	public Job job2(ApplicationContext ac) {
		this.getClass().getAnnotations();
		return jobBuilderFactory
				.get("test_job_2")
				.start(step(ac))
				.build();
	}
	
	@Bean("testStep")
	public Step step(ApplicationContext ac) {
		return stepBuilderFactory
				.get("testStep").<BhJob, BhJob>chunk(3)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}
