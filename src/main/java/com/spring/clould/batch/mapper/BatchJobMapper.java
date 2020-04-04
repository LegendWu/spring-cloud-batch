package com.spring.clould.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.BatchJob;

/**
 * Description: 任务mapper
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public interface BatchJobMapper extends BaseMapper<BatchJob> {
	
	/**
	 * 更新任务状态或者实例ID
	 * @param job
	 * @return
	 */
	int updateJobStatusOrInstanceId(BatchJob job);
}