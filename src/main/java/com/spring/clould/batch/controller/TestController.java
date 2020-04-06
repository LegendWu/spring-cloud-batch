package com.spring.clould.batch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.CatMapper;
import com.spring.clould.batch.util.RedisLockUtil;

/**
 * Description: 测试controller
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@RestController
public class TestController {

	@Autowired
	CatMapper catMapper;

	@Autowired
	RedisLockUtil redisLockUtil;

//	@PostConstruct
	public void initCat() {
		for (int i = 0; i < 10080; i++) {
			Cat cat = new Cat();
			cat.setCatname("cat" + i);
			cat.setCatage(i + "");
			cat.setCataddress("cat address " + i);
			catMapper.insert(cat);
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}
	}

	@GetMapping("deleteLock")
	public void deleteRedisLock(@RequestParam String key) {
		redisLockUtil.deleleJobLock(key);
		redisLockUtil.deleleJobRetryLock(key);
	}

}
