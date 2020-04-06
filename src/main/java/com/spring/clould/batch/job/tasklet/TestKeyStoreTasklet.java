package com.spring.clould.batch.job.tasklet;

import java.util.HashMap;
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
import com.spring.clould.batch.entity.BatchJobConfig;
import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.BatchJobConfigMapper;
import com.spring.clould.batch.mapper.CatMapper;
import com.spring.clould.batch.service.ICatService;

/**
 * Description: 测试类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class TestKeyStoreTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(TestKeyStoreTasklet.class);
	
	private List<Integer> keyList;
	
	@Autowired
	CatMapper catMapper;
	
	@Autowired
	ICatService catService;
	
	@Autowired
	BatchJobConfigMapper batchJobConfigMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(null == keyList) {
			return RepeatStatus.FINISHED; 
		}
		List<Cat> cats = catMapper.selectBatchIds(keyList);
		for(Cat cat : cats) {
			cat.setIsKeyStore(cat.getIsKeyStore()+1);
		}
		catService.updateBatchById(cats);
		logger.info("key store 任务执行中，当前分片fromId="+keyList.get(0)+", toId="+keyList.get(keyList.size()-1)+", keyList.size()="+keyList.size());
		
		//测试异常
		Map<String, Object> param1 = new HashMap<String, Object>();
		param1.put("confCode", "TEST_ERROR_SWITCH");
		BatchJobConfig value = batchJobConfigMapper.selectConfigByCode(param1);
		if(value.getConfValue().equals("Y")) {
			Map<String, Object> param2 = new HashMap<String, Object>();
			param2.put("confCode", "TEST_ERROR_DATA");
			BatchJobConfig value2 = batchJobConfigMapper.selectConfigByCode(param2);
			String[] ids = value2.getConfValue().split(",");
			for(String id : ids) {
				if(keyList.get(0) == Integer.parseInt(id)) {
					throw new RuntimeException("测试异常");
				}
			}
		}
		
		keyList = null;
		cats = null;
		return RepeatStatus.FINISHED;
	}
	
	public TestKeyStoreTasklet(String keyList) {
		if(!StringUtils.isEmpty(keyList)) {
			this.keyList = JSONArray.parseArray(keyList, Integer.class);
		}
	}

}
