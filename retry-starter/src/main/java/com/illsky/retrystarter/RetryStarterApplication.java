package com.illsky.retrystarter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.illsky"})
@MapperScan("com.illsky.retry.mapper")
@SpringBootApplication
public class RetryStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetryStarterApplication.class, args);
	}

}
