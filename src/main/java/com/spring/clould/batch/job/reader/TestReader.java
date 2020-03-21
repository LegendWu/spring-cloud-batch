package com.spring.clould.batch.job.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.BhJob;

@Component
public class TestReader implements ItemReader<BhJob>{

	@Override
	public BhJob read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("正在执行reader");
		return null;
	}

}
