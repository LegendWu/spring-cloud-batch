package com.spring.clould.batch.job.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.BhJob;

@Component
public class TestWriter implements ItemWriter<BhJob> {

	@Override
	public void write(List<? extends BhJob> items) throws Exception {
		System.out.println("正在执行writer");
	}

}
