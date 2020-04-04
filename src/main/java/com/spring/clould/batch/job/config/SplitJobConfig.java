package com.spring.clould.batch.job.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class SplitJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
	Step testKeyRangeMasterStep;
	
	@Autowired
	Step testKeyStoreMasterStep;
	
	@Autowired
	Step testEntityStoreMasterStep;

    /**
              * 创建job运行Flow,我们利用split(new
     * SimpleAsyncTaskExecutor()).add()让flow异步执行,add()中可以添加多个Flow
     * @return
     */
    @Bean
    public Job spiltJob() {
        return jobBuilderFactory.get("spiltJob")
        		.start(jobSpiltFlow1()).split(new SimpleAsyncTaskExecutor()).add(jobSpiltFlow2())
                .next(jobSpiltStep1())
                .next(jobSpiltStep2())
                .next(jobSpiltStep3())
                .next(jobSpiltStep4())
                .end()
                .build();

    }

    // 创建Flow1
    @Bean
    public Flow jobSpiltFlow1() {
        return new FlowBuilder<Flow>("jobSpiltFlow1")
                .start(testKeyRangeMasterStep)
                .next(testKeyStoreMasterStep)
                .build();

    }

    // 创建Flow1
    @Bean
    public Flow jobSpiltFlow2() {
        return new FlowBuilder<Flow>("jobSpiltFlow2")
                .start(testEntityStoreMasterStep)
                .build();

    }
    
    @Bean
    public Step jobSpiltStep1() {
        return stepBuilderFactory.get("jobSpiltStep1").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            	System.out.println("Hello-->jobSpiltStep1-->start");
            	Thread.sleep(2000);
                System.out.println("Hello-->jobSpiltStep1-->end");
                return RepeatStatus.FINISHED;
            }
        }).build();

    }
    
    @Bean
    public Step jobSpiltStep2() {
        return stepBuilderFactory.get("jobSpiltStep2").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            	System.out.println("Hello-->jobSpiltStep2-->start");
            	Thread.sleep(2000);
                System.out.println("Hello-->jobSpiltStep2-->end");
                return RepeatStatus.FINISHED;
            }
        }).build();

    }
    
    @Bean
    public Step jobSpiltStep3() {
        return stepBuilderFactory.get("jobSpiltStep3").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            	System.out.println("Hello-->jobSpiltStep3-->start");
            	Thread.sleep(2000);
                System.out.println("Hello-->jobSpiltStep3-->end");
                return RepeatStatus.FINISHED;
            }
        }).build();

    }
    
    @Bean
    public Step jobSpiltStep4() {
        return stepBuilderFactory.get("jobSpiltStep4").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            	System.out.println("Hello-->jobSpiltStep4-->start");
            	Thread.sleep(2000);
                System.out.println("Hello-->jobSpiltStep4-->end");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

}
