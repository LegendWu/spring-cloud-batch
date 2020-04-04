package com.spring.clould.batch.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockUtil {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public static final String LOCK_JOB_PREFIX = "LOCK_JOB_"; // 任务分布式锁
	public static final String LOCK_JOB_REFRESH = "LOCK_JOB_REFRESH"; // 定时刷新任务分布式锁
	public static final int LOCK_JOB_EXPIRE = 28800000; // 分布式锁失效时间8小时
	public static final int LOCK_JOB_REFRESH_EXPIRE = 15000; // 分布式锁失效时间15S
	
	/**
	 * 分布式锁
	 * @param key key值
	 * @return 是否获取到
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean lock(String key, int expire) {
		// 利用lambda表达式
		return (Boolean) redisTemplate.execute((RedisCallback) connection -> {
			long expireAt = System.currentTimeMillis() + expire + 1;
			Boolean acquire = connection.setNX(key.getBytes(), String.valueOf(expireAt).getBytes());
			if (acquire) {
				return true;
			} else {
				byte[] value = connection.get(key.getBytes());
				if (Objects.nonNull(value) && value.length > 0) {
					long expireTime = Long.parseLong(new String(value));
					if (expireTime < System.currentTimeMillis()) {
						// 如果锁已经过期
						byte[] oldValue = connection.getSet(key.getBytes(),
								String.valueOf(System.currentTimeMillis() + expire + 1).getBytes());
						// 防止死锁
						return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
					}
				}
			}
			return false;
		});
	}

	/**
	 * 任务分布式锁
	 * @param jobName
	 * @return
	 */
	public boolean lockJob(String jobName) {
		return lock(LOCK_JOB_PREFIX+jobName, LOCK_JOB_EXPIRE);
	}
	
	/**
	 * 任务刷新分布式锁
	 * @return
	 */
	public boolean lockJobRefresh() {
		return lock(LOCK_JOB_REFRESH, LOCK_JOB_REFRESH_EXPIRE);
	}
	
	/**
	 * 删除分布式锁
	 * 
	 * @param key
	 */
	public void deleleJobLock(String jobName) {
		redisTemplate.delete(LOCK_JOB_PREFIX+jobName);
	}
	
	/**
	 * 删除刷新任务分布式锁
	 * 
	 * @param key
	 */
	public void deleleJobRefreshLock() {
		redisTemplate.delete(LOCK_JOB_REFRESH);
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
