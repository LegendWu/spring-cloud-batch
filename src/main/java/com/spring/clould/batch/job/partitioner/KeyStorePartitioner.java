package com.spring.clould.batch.job.partitioner;

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

public class KeyStorePartitioner<T> extends SimplePartitioner {

	private Logger logger = LoggerFactory.getLogger(KeyStorePartitioner.class);
	
	private SqlSessionFactory sqlSessionFactory;

	private SqlSessionTemplate sqlSessionTemplate;
	
	private String queryId;
	
	private Map<String, Object> parameters;
	
	public KeyStorePartitioner(SqlSessionFactory sqlSessionFactory, String queryId, Map<String, Object> parameters) {
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
		if(CollectionUtils.isEmpty(result) || result.size() < gridSize) {
			gridSize = 1;
		}else {
			gridSize = result.size()%gridSize>0 ? result.size()/gridSize+1 : result.size()/gridSize;
		}
		logger.info("列表大小[ {} ]，分片大小[ {} ]", null==result?0:result.size(), gridSize);
		Map<String, ExecutionContext> partitions = super.partition(gridSize);
		if(CollectionUtils.isEmpty(result)) {
			return partitions;
		}
		if(result.size() < gridSize) {
			ExecutionContext context = partitions.values().iterator().next();
			context.put("keyList", JSONArray.toJSONString(result));
			return partitions;
		}else {
			int i = 0;
			List<List<T>> lists = SeparateUtil.separateList(result, gridSize);
			for (ExecutionContext context : partitions.values()) {
				context.put("keyList", JSONArray.toJSONString(lists.get(i)));
				i++;
			}
			lists = null;
		}
		result = null;
		return partitions;
	}
	
}
