package com.spring.clould.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.spring.clould.batch.mapper")
public class BatchApp {

	public static void main(String[] args) {
		SpringApplication.run(BatchApp.class, args);
	}

}