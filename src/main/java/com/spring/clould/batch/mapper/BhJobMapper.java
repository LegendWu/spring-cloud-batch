package com.spring.clould.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.BhJob;

public interface BhJobMapper extends BaseMapper<BhJob> {
	/**
	 * 自定义方法，删除所有任务
	 * @return
	 */
	int deleteAll();
}