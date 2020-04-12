package com.spring.clould.batch.job.common.reader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;

/**
 * Description: 测试类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class CommonItemReader implements ItemReader<Integer> {
	
	private Logger logger = LoggerFactory.getLogger(CommonItemReader.class);
	
	private List<Integer> keyList;
	
	public CommonItemReader(String keyListStr) {
		if(!StringUtils.isEmpty(keyListStr)) {
			this.keyList = JSONArray.parseArray(keyListStr, Integer.class);
			logger.info("当前分片fromId="+keyList.get(0)+", toId="+keyList.get(keyList.size()-1)+", keyList.size()="+keyList.size());
		}
	}

	@Override
	public synchronized Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		while(!CollectionUtils.isEmpty(keyList)) {
			Integer key = keyList.get(0);
			keyList.remove(0);
			return key;
		}
		return null;
	}

}
