package com.spring.clould.batch.job.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.BatchJobConfigMapper;
import com.spring.clould.batch.mapper.CatMapper;
import com.spring.clould.batch.service.ICatService;

/**
 * Description: 测试类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class TestKeyStoreWriter implements ItemWriter<Integer> {
	
	private Logger logger = LoggerFactory.getLogger(TestKeyStoreWriter.class);
	
	@Autowired
	CatMapper catMapper;
	
	@Autowired
	ICatService catService;
	
	@Autowired
	BatchJobConfigMapper batchJobConfigMapper;

	@Override
	public void write(List<? extends Integer> items) throws Exception {
		List<Cat> cats = catMapper.selectBatchIds(items);
		for(Cat cat : cats) {
			cat.setIsKeyStore(cat.getIsKeyStore()+1);
		}
		catService.updateBatchById(cats);
		logger.debug("线程{}更新数量：{}", Thread.currentThread().getName(), cats.size());
	}

}
