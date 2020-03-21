package com.spring.clould.batch.job.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.BhJob;

@Component
public class TestProcessor implements ItemProcessor<BhJob, BhJob>{

	@Override
	public BhJob process(BhJob item) throws Exception {
		System.out.println("正在执行processor");
		return null;
	}

}
