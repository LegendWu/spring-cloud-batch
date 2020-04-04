package com.spring.clould.batch.job.tasklet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.CatMapper;

public class TestKeyRangeTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(TestKeyRangeTasklet.class);
	
	private int fromId;
	
	private int toId;
	
	@Autowired
	CatMapper catMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fromId", fromId);
		param.put("toId", toId);
		List<Cat> cats = catMapper.selectByIdRange(param);
		logger.info("key range 任务执行中，当前分片fromId="+fromId+", toId="+toId+", size="+cats.size());
		return RepeatStatus.FINISHED;
	}
	
	public TestKeyRangeTasklet(int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
	}

}
