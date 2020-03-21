package com.spring.clould.batch.mapper;

import com.spring.clould.batch.entity.BhJob;
import com.spring.clould.batch.mapper.base.SuperMapper;

public interface BhJobMapper extends SuperMapper<BhJob> {
	/**
	 * 自定义方法，删除所有任务
	 * @return
	 */
	int deleteAll();
}