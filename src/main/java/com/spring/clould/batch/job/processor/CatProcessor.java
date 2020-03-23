package com.spring.clould.batch.job.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.Cat;

@Component
public class CatProcessor implements ItemProcessor<Cat, Cat> {
	
	private Logger logger = LoggerFactory.getLogger(CatProcessor.class);
	
	@Override
	public Cat process(Cat item) throws Exception {
		logger.info("处理中...id="+item.getId()+", name="+item.getCatname());
		return item;
	}
}
