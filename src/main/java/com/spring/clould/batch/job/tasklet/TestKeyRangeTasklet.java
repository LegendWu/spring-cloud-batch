package com.spring.clould.batch.job.tasklet;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
public class TestKeyRangeTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(TestKeyRangeTasklet.class);
	
	private Map<String, Object> keyMap;
	
	@Autowired
	CatMapper catMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(null == keyMap) {
			return RepeatStatus.FINISHED;
		}
		List<Cat> cats = catMapper.selectByIdRange(keyMap);
		logger.info("key range 任务执行中，当前分片fromId="+keyMap.get("fromId")+", toId="+keyMap.get("toId")+", size="+cats.size());
		return RepeatStatus.FINISHED;
	}
	
	public TestKeyRangeTasklet(String keyMap) {
		if(!StringUtils.isEmpty(keyMap)) {
			this.keyMap = JSONArray.parseObject(keyMap);
		}
	}

}
