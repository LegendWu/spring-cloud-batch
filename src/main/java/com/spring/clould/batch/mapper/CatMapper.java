package com.spring.clould.batch.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.Cat;

/**
 * Description: 测试mapper
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public interface CatMapper extends BaseMapper<Cat> {

	List<Integer> loadKeys(Map<String, Object> param);
	
	List<Cat> loadAllCats(Map<String, Object> param);
	
	List<Cat> selectByIdRange(Map<String, Object> param);
	
}