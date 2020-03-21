package com.spring.clould.batch.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring.clould.batch.entity.BhJob;

public class ConvertUtil {

	private static Logger logger = LoggerFactory.getLogger(ConvertUtil.class);

	/**
	 * job对象转map
	 * 
	 * @param job
	 * @return
	 */
	public static JobDataMap convertToJobDataMap(BhJob job) {
		JobDataMap map = new JobDataMap();
		Field[] fields = job.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(job));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("convert failed!");
			}
		}
		return map;
	}

	/**
	 * map转对象
	 * @param map
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static BhJob convertToBhJob(JobDataMap map) {
		if (map == null)
			return null;
		BhJob job = new BhJob();
		Field[] fields = job.getClass().getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
				continue;
			}
			field.setAccessible(true);
			try {
				if(!field.getName().equals("status")) {
					field.set(job, map.get(field.getName()));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return job;
	}
}
