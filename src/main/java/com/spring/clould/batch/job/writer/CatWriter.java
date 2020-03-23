package com.spring.clould.batch.job.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class CatWriter<Cat> implements ItemWriter<Cat> {
	
	private Logger logger = LoggerFactory.getLogger(CatWriter.class);
	
	@Override
	public void write(List<? extends Cat> items) throws Exception {
		if(null != items) {
			logger.info("正在写，列表大小="+items.size());
		}
	}

}
