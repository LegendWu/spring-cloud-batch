package com.spring.clould.batch.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.Cat;

public interface CatMapper extends BaseMapper<Cat> {

	List<Integer> loadKeys(Map<String, Object> param);
	
	List<Cat> loadAllCats(Map<String, Object> param);
	
	List<Cat> selectByIdRange(Map<String, Object> param);
	
}