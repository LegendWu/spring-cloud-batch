package com.spring.clould.batch.job.step;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.job.partitioner.KeyRangePartitioner;
import com.spring.clould.batch.job.processor.CatProcessor;
import com.spring.clould.batch.job.reader.CommonRangeKeyReader;
import com.spring.clould.batch.job.writer.CommonPartitionFileItemWriter;

@Configuration
@EnableBatchProcessing
public class CatLocalPartitionerJob {
	
	private static final int GRID_SIZE = 3;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
	@StepScope
	CatProcessor catProcessor;

    @Bean("catLocalReaderProcessorWriterJob")
    public Job job() {
         return jobBuilderFactory.get("catLocalReaderProcessorWriterJob")
                 .start(catMasterStep())
                 .build();
    }

    @Bean
    public Step catMasterStep() {
    	String queryId = "com.spring.clould.batch.mapper.CatMapper.loadKeys";
        return stepBuilderFactory
        		.get("catMasterStep")
        		.partitioner(catSlaveStep().getName(), new KeyRangePartitioner<Integer>(sqlSessionFactory, queryId, null))
                .partitionHandler(catPartitionHandler()).build();
    }

    @Bean
    public PartitionHandler catPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(GRID_SIZE);
        handler.setTaskExecutor(catPartitionHandlerTaskExecutor());
        handler.setStep(catSlaveStep());
        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }

    @Bean
    public SimpleAsyncTaskExecutor catPartitionHandlerTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Step catSlaveStep() {
        return stepBuilderFactory.get("catSlaveStep")
                .<Cat, Cat>chunk(10)
                .reader(catLocalRangeKeyReader(null, null))
                .processor(catProcessor)
                .writer(commonPartitionFileItemWriter(null, null))
                .build();
    }

    @Bean
	@StepScope
	public CommonRangeKeyReader<Cat> catLocalRangeKeyReader(@Value("#{stepExecutionContext[startId]}") final Integer fromId,
			@Value("#{stepExecutionContext[endId]}") final Integer toId) {
		String queryId = "com.spring.clould.batch.mapper.CatMapper.selectByIdRange";
		return new CommonRangeKeyReader<Cat>(sqlSessionFactory, queryId, fromId, toId);
	}

    @Bean
    @StepScope
    public CommonPartitionFileItemWriter<Cat> commonPartitionFileItemWriter(
    		@Value("#{stepExecutionContext[startId]}") final String startId,
            @Value("#{stepExecutionContext[endId]}") final String endId) {
         return new CommonPartitionFileItemWriter<Cat>(Cat.class, startId, endId);
    }
}
