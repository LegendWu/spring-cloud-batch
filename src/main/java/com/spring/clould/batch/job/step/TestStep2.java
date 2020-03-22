//package com.spring.clould.batch.job.step;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
//import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
//import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.MessageChannel;
//
//import com.spring.clould.batch.job.listener.TestListener;
//import com.spring.clould.batch.job.reader.TestReader;
//
//@Configuration
//@EnableBatchProcessing
//@EnableBatchIntegration
//public class TestStep2 {
//	
//	@Autowired
//	JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory;
//
//	@Autowired
//	RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
//
//	@Autowired
//	TestReader reader;
//
//	@Autowired
//	ItemProcessor<? super Object, ? extends Object> processor;
//
//	@Autowired
//	ItemWriter<? super Object> writer;
//
//	@Autowired
//	TestListener listener;
//	
//	@Bean("test_job_1")
//	public Job job1() {
//		return jobBuilderFactory
//				.get("test_job_1")
//				.listener(listener)
//				.start(masterStep())
//				.build();
//	}
//
//	@Bean
//	public Step masterStep() {
//		return this.masterStepBuilderFactory.get("masterStep").partitioner("workerStep", partitioner()).gridSize(10)
//				.outputChannel(outgoingRequestsToWorkers()).inputChannel(incomingRepliesFromWorkers()).build();
//	}
//
//	@Bean
//	public Step workerStep() {
//		return this.workerStepBuilderFactory.get("workerStep").inputChannel(incomingRequestsFromMaster())
//				.outputChannel(outgoingRepliesToMaster()).chunk(100).reader(reader).processor(processor)
//				.writer(writer).build();
//	}
//	
//	@Bean
//	public Partitioner partitioner() {
//		//TODO
//		return null;
//	}
//	
//	@Bean
//	public MessageChannel outgoingRequestsToWorkers() {
//		//TODO
//		return null;
//	}
//	
//	@Bean
//	public MessageChannel incomingRepliesFromWorkers() {
//		//TODO
//		return null;
//	}
//	
//	@Bean
//	public MessageChannel incomingRequestsFromMaster() {
//		//TODO
//		return null;
//	}
//	
//	@Bean
//	public MessageChannel outgoingRepliesToMaster() {
//		//TODO
//		return null;
//	}
//}
