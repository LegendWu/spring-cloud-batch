package com.spring.clould.batch.mapper;

import com.spring.clould.batch.entity.BhJobStep;
import com.spring.clould.batch.mapper.base.SuperMapper;

public interface BhJobStepMapper extends SuperMapper<BhJobStep> {
	/**
	 * 自定义方法，删除所有步骤
	 * @return
	 */
	int deleteAll();
}