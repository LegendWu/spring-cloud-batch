package com.spring.clould.batch.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
