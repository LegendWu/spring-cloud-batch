package com.spring.clould.batch.job.common.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ExecutionContext;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.spring.clould.batch.util.SeparateUtil;

/**
 * Description: key区间分片
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class KeyRangePartitioner<T> extends SimplePartitioner {

	private Logger logger = LoggerFactory.getLogger(KeyRangePartitioner.class);
	
	private SqlSessionFactory sqlSessionFactory;

	private SqlSessionTemplate sqlSessionTemplate;
	
	private String queryId;
	
	private Map<String, Object> parameters;
	
	public KeyRangePartitioner(SqlSessionFactory sqlSessionFactory, String queryId, Map<String, Object> parameters) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.queryId = queryId;
		this.parameters = parameters;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		if (sqlSessionTemplate == null) {
			sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
	    }
		List<T> result = sqlSessionTemplate.selectList(queryId, parameters);
		if(CollectionUtils.isEmpty(result)) {
			gridSize = 0;
		} else if (result.size() < gridSize) {
			gridSize = 1;
		} else {
			gridSize = result.size()%gridSize>0 ? result.size()/gridSize+1 : result.size()/gridSize;
		}
		logger.info("列表大小[ {} ]，分片大小[ {} ]", null==result?0:result.size(), gridSize);
		Map<String, ExecutionContext> partitions = super.partition(gridSize);
		List<Map<String, T>> rangeList = SeparateUtil.separateListRange(result, gridSize);
		int i = 0;
		for (ExecutionContext context : partitions.values()) {
			Map<String, Object> keyMap = new HashMap<String, Object>();
			keyMap.put("fromId", rangeList.get(i).get("fromId"));
			keyMap.put("toId", rangeList.get(i).get("toId"));
			context.put("keyMap", JSONArray.toJSONString(keyMap));
			i++;
		}
		rangeList = null;
		result = null;
		return partitions;
	}
	
}
