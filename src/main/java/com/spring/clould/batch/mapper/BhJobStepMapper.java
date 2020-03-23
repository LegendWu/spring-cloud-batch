package com.spring.clould.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.clould.batch.entity.BhJobStep;

public interface BhJobStepMapper extends BaseMapper<BhJobStep> {
	/**
	 * 自定义方法，删除所有步骤
	 * @return
	 */
	int deleteAll();
}