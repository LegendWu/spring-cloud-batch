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

	public static final String LOCK_PREFIX = "redis_lock";
	public static final int LOCK_EXPIRE = 3600000; // ms

	/**
	 * 最终加强分布式锁
	 *
	 * @param key key值
	 * @return 是否获取到
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean lock(String key) {
		String lock = LOCK_PREFIX + key;
		// 利用lambda表达式
		return (Boolean) redisTemplate.execute((RedisCallback) connection -> {
			long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;
			Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
			if (acquire) {
				return true;
			} else {
				byte[] value = connection.get(lock.getBytes());
				if (Objects.nonNull(value) && value.length > 0) {
					long expireTime = Long.parseLong(new String(value));
					if (expireTime < System.currentTimeMillis()) {
						// 如果锁已经过期
						byte[] oldValue = connection.getSet(lock.getBytes(),
								String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());
						// 防止死锁
						return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
					}
				}
			}
			return false;
		});
	}

	/**
	 * 删除分布式锁
	 * @param key
	 */
	public void delete(String key) {
		redisTemplate.delete(LOCK_PREFIX + key);
	}
}
