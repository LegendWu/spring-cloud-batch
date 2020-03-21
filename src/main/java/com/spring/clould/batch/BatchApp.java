package com.spring.clould.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.spring.clould.batch.util.BeanUtil;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.spring.clould.batch.mapper")
public class BatchApp {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BatchApp.class, args);
		BeanUtil.setContext(context);
	}

}