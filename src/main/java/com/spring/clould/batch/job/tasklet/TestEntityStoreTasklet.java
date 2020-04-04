package com.spring.clould.batch.job.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.CatMapper;

/**
 * Description: 测试类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class TestEntityStoreTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(TestEntityStoreTasklet.class);
	
	private List<Cat> keyList;
	
	@Autowired
	CatMapper catMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("entity store 任务执行中，当前分片fromId="+keyList.get(0).getId()+", toId="+keyList.get(keyList.size()-1).getId()+", keyList.size()="+keyList.size());
		return RepeatStatus.FINISHED;
	}
	
	public TestEntityStoreTasklet(String keyList) {
		this.keyList = JSONArray.parseArray(keyList, Cat.class);
	}

}
