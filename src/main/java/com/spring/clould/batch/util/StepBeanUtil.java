package com.spring.clould.batch.util;

import org.springframework.batch.core.Step;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Description: BeanUtil
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@Component
public class StepBeanUtil implements ApplicationContextAware{
	
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public DirectChannel getMasterChannel(Class<?> clz) {
		return (DirectChannel) applicationContext.getBean(ClassUtils.getShortName(clz)+".masterChannel");
	}
	
	public DirectChannel getWorkerChannel(Class<?> clz) {
		return (DirectChannel) applicationContext.getBean(ClassUtils.getShortName(clz)+".workerChannel");
	}
	
	public static String getMasterChannelName(Class<?> clz) {
		return ClassUtils.getShortName(clz)+".masterChannel";
	}
	
	public static String getWorkerChannelName(Class<?> clz) {
		return ClassUtils.getShortName(clz)+".workerChannel";
	}
	
	public Step getMasterStep(Class<?> clz) {
		return (Step) applicationContext.getBean(ClassUtils.getShortName(clz)+".masterStep");
	}
}
