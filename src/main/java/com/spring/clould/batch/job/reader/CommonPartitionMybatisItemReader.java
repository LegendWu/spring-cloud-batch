package com.spring.clould.batch.job.reader;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;

public class CommonPartitionMybatisItemReader<T> extends MyBatisPagingItemReader<T> {

	public CommonPartitionMybatisItemReader(SqlSessionFactory sqlSessionFactory, String name, String fromId,
			String toId) {
		setSqlSessionFactory(sqlSessionFactory);
//		setQueryId("com.spring.cloud.batch.entity." + name + ".selectPartitionList");
		setQueryId("com.spring.clould.batch.mapper."+ name +"Mapper.selectPartitionList");
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("fromId", fromId);
		parameterValues.put("toId", toId);
		setParameterValues(parameterValues);
		setPageSize(100);
	}
}
