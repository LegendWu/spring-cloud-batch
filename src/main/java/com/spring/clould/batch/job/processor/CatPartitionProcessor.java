package com.spring.clould.batch.job.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.Cat;

@Component
@StepScope
public class CatPartitionProcessor implements ItemProcessor<Cat, Cat> {
	
	private Logger logger = LoggerFactory.getLogger(CatPartitionProcessor.class);
	
	@Override
	public Cat process(Cat item) throws Exception {
		logger.info("处理中。。。"+item.getCatname());
		return item;
	}
}
