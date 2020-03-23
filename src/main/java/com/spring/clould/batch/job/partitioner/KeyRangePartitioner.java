package com.spring.clould.batch.job.partitioner;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ExecutionContext;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

public class KeyRangePartitioner<T> extends SimplePartitioner {

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
		Map<String, ExecutionContext> partitions = super.partition(gridSize);
		if (sqlSessionTemplate == null) {
			sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
	    }
		List<T> result = sqlSessionTemplate.selectList(queryId, parameters);
		if(CollectionUtils.isEmpty(result)) {
			return partitions;
		}
		if(result.size() < gridSize) {
			ExecutionContext context = partitions.values().iterator().next();
			context.put("startId", result.get(0));
			context.put("endId", result.get(result.size()-1));
			return partitions;
		}
		int i = 0;
		int subListSize = result.size()/gridSize;
		for (ExecutionContext context : partitions.values()) {
			context.put("startId", result.get(i*subListSize));
			if(i == partitions.values().size()-1) {
				context.put("endId", result.get(result.size()-1));
			}else {
				context.put("endId", result.get(((i+1)*subListSize)-1));
			}
			i++;
		}
		return partitions;
	}
	
}
