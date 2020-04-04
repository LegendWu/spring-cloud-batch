package com.spring.clould.batch.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: BeanUtil
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
public class BeanUtil {
	
	private static ApplicationContext context;
	
	public static ApplicationContext getContext() {
		return context;
	}
	
	public static void setContext(ApplicationContext context) {
		BeanUtil.context = context;
	}
	
	public static void initContext(String xmlFileName) {
		context = new ClassPathXmlApplicationContext(xmlFileName);
	}
}
