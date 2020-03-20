package com.spring.clould.batch.job.base;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.entity.BhJobStep;
import com.spring.clould.batch.mapper.BhJobMapper;
import com.spring.clould.batch.mapper.BhJobStepMapper;

/**
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@Component
public class QuartzJob implements Job {
    private Logger logger = LoggerFactory.getLogger(QuartzJob.class);
    
    @Autowired
    BhJobMapper bhJobMapper;
    
    @Autowired
    BhJobStepMapper bhJobStepMapper;
    
    @Override
    public void execute(JobExecutionContext executorContext) throws JobExecutionException {
        JobDataMap map = executorContext.getMergedJobDataMap();
        BhJob job = bhJobMapper.selectById(map.getLongValue("id"));
        logger.info("正在执行任务{}", job.getJobName());
        List<BhJobStep> steps = bhJobStepMapper.selectList(new QueryWrapper<BhJobStep>().orderByAsc("step_name"));
        logger.info("当前任务下可执行的步骤有{}", steps.size());
    }
}