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
import com.spring.clould.batch.job.partitioner.CommonPartitioner;
import com.spring.clould.batch.job.processor.CatPartitionProcessor;
import com.spring.clould.batch.job.reader.CommonPartitionMybatisItemReader;
import com.spring.clould.batch.job.writer.CommonPartitionFileItemWriter;

@Configuration
@EnableBatchProcessing
public class CatMasterPartitionerJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CommonPartitioner catPartitioner;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CatPartitionProcessor catPartitionProcessor;

    @Bean("test_job_1")
    public Job catPartitionerJob() {
         return jobBuilderFactory.get("test_job_1")
                 .start(catMasterStep())
                 .build();
    }

    @Bean
    public Step catMasterStep() {
        return stepBuilderFactory
        		.get("catMasterStep")
        		.partitioner(catSlaveStep().getName(), catPartitioner)
                .partitionHandler(catPartitionHandler()).build();
    }

    @Bean
    public PartitionHandler catPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(2);
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
                .reader(commonPartitionMybatisItemReader(null, null))
                .processor(catPartitionProcessor)
                .writer(commonPartitionFileItemWriter(null, null))
                .build();
    }

    @Bean
    @StepScope
    public CommonPartitionMybatisItemReader<Cat> commonPartitionMybatisItemReader( 
    		@Value("#{stepExecutionContext[fromId]}") final String fromId,
            @Value("#{stepExecutionContext[toId]}") final String toId) {
        return new CommonPartitionMybatisItemReader<Cat>(sqlSessionFactory, Cat.class.getSimpleName(), fromId, toId);
    }

    @Bean
    @StepScope
    public CommonPartitionFileItemWriter<Cat> commonPartitionFileItemWriter(
    		@Value("#{stepExecutionContext[fromId]}") final String fromId,
            @Value("#{stepExecutionContext[toId]}") final String toId) {
         return new CommonPartitionFileItemWriter<Cat>(Cat.class, fromId, toId);
    }
}
