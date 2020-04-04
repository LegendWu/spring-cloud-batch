package com.spring.clould.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.spring.clould.batch.util.RedisLockUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchAppTests {

	@Autowired
	RedisLockUtil redisLockUtil;
	
	@Test
	public void deleteRedisLock() {
		redisLockUtil.delete("testJob");
	}

}
