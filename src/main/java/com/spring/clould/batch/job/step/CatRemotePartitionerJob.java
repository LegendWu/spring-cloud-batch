package com.spring.clould.batch.job.step;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.job.partitioner.KeyRangePartitioner;
import com.spring.clould.batch.job.processor.CatProcessor;
import com.spring.clould.batch.job.reader.CommonRangeKeyReader;
import com.spring.clould.batch.job.writer.CatWriter;
import com.spring.clould.batch.mapper.CatMapper;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class CatRemotePartitionerJob {

	private static final int GRID_SIZE = 3;

	@Autowired
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	RemotePartitioningMasterStepBuilderFactory managerStepBuilderFactory;

	@Autowired
	RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;

	@Autowired
	@StepScope
	CatProcessor catProcessor;

	@Autowired
	@StepScope
	CatWriter<Cat> catWriter;

	@Autowired
	CatMapper catMapper;

	@Bean("catRequests")
	public DirectChannel catRequests() {
		return new DirectChannel();
	}

	@Bean("catOutboundFlow")
	public IntegrationFlow catOutboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(catRequests())
				.handle(Jms.outboundAdapter(connectionFactory).destination("catRequests")).get();
	}

	@Bean("catManagerStep")
	public Step catManagerStep() {
		String queryId = "com.spring.clould.batch.mapper.CatMapper.loadKeys";
		return this.managerStepBuilderFactory.get("catManagerStep")
				.partitioner("catWorkerStepReaderProcessorWriter", new KeyRangePartitioner<Integer>(sqlSessionFactory, queryId, null))
				.gridSize(GRID_SIZE).outputChannel(catRequests()).build();
	}

	@Bean("catRemoteReaderProcessorWriterJob")
	public Job job() {
		return this.jobBuilderFactory.get("catRemoteReaderProcessorWriterJob").start(catManagerStep()).build();
	}

	@Bean("requestsWorker")
	public DirectChannel requestsWorker() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("catRequests"))
				.channel(requestsWorker()).get();
	}

	@Bean
	public Step catWorkerStepReaderProcessorWriter() {
		return this.workerStepBuilderFactory.get("catWorkerStepReaderProcessorWriter").inputChannel(requestsWorker()).<Cat, Cat>chunk(10)
				.reader(catRemoteRangeKeyReader(null, null)).processor(catProcessor).writer(catWriter).build();
	}

//	@Bean
//	public Step catWorkerStepTasklet() {
//		return this.workerStepBuilderFactory.get("catWorkerStepTasklet").inputChannel(requestsWorker())
//				.tasklet(catRangeKeyTasklet(null, null)).build();
//	}

	@Bean
	@StepScope
	public CommonRangeKeyReader<Cat> catRemoteRangeKeyReader(@Value("#{stepExecutionContext[startId]}") final Integer fromId,
			@Value("#{stepExecutionContext[endId]}") final Integer toId) {
		String queryId = "com.spring.clould.batch.mapper.CatMapper.selectByIdRange";
		return new CommonRangeKeyReader<Cat>(sqlSessionFactory, queryId, fromId, toId);
	}

//	@Bean
//	@StepScope
//	public CatTasklet catRangeKeyTasklet(@Value("#{stepExecutionContext[startId]}") final Integer fromId,
//			@Value("#{stepExecutionContext[endId]}") final Integer toId) {
//		return new CatTasklet(fromId, toId);
//	}

}
