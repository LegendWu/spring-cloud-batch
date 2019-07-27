package com.spring.clould.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.spring.clould.batch.dao")
public class BatchApp {

	public static void main(String[] args) {
		SpringApplication.run(BatchApp.class, args);
	}

}

