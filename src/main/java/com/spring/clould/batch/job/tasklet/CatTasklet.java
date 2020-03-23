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

public class CatTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(CatTasklet.class);
	
	private int startId;
	
	private int endId;
	
	@Autowired
	CatMapper catMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("tasklet任务执行中，当前分片startId= " + startId + ", endId="+endId);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("startId", startId);
		param.put("endId", endId);
		List<Cat> cats = catMapper.selectByIdRange(param);
		for(Cat cat: cats) {
			logger.info(cat.getCatname());
		}
		return RepeatStatus.FINISHED;
	}
	
	public CatTasklet(int startId, int endId) {
		this.startId = startId;
		this.endId = endId;
	}

}
