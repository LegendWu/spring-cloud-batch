package com.spring.clould.batch.mapper;

import java.util.List;
import java.util.Map;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.base.SuperMapper;

public interface CatMapper extends SuperMapper<Cat> {

	List<Cat> selectPartitionList(Map<String, Object> param);
}