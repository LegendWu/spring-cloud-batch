package com.spring.clould.batch.job.reader;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;

public class CommonRangeKeyReader<T> extends MyBatisPagingItemReader<T> {

	public CommonRangeKeyReader(SqlSessionFactory sqlSessionFactory, String queryId, int startId,
			int endId) {
		setSqlSessionFactory(sqlSessionFactory);
		setQueryId(queryId);
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("startId", startId);
		parameterValues.put("endId", endId);
		setParameterValues(parameterValues);
	}
	
	public CommonRangeKeyReader(SqlSessionFactory sqlSessionFactory, String queryId, String startId,
			String endId) {
		setSqlSessionFactory(sqlSessionFactory);
		setQueryId(queryId);
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("startId", startId);
		parameterValues.put("endId", endId);
		setParameterValues(parameterValues);
	}
}