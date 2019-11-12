package com.test.datasource.doubleSource;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.test.datasource.doubleSource.user.dao")
public class DoubleSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoubleSourceApplication.class, args);
	}

}
