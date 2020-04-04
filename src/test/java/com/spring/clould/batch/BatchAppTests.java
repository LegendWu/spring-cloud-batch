package com.spring.clould.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.spring.clould.batch.util.RedisLockUtil;

/**
 * Description: 测试启动类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchAppTests {

	@Autowired
	RedisLockUtil redisLockUtil;
	
	@Test
	public void deleteRedisLock() {
		redisLockUtil.deleleJobLock("testJob");
	}

}
