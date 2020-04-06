package com.spring.clould.batch.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.BatchJobConfig;

/**
 * Description: 任务mapper
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public interface BatchJobConfigMapper extends BaseMapper<BatchJobConfig> {
	
	BatchJobConfig selectConfigByCode(Map<String, Object> param);
	
}