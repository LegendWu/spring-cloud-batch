package com.spring.clould.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.BhJob;

/**
 * Description: 任务mapper
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public interface BhJobMapper extends BaseMapper<BhJob> {
	/**
	 * 自定义方法，删除所有任务
	 * @return
	 */
	int deleteAll();
}