package com.sly.fescar.account;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 工程启动类
 * 
 * @author sly
 * @time 2019年6月10日
 */
@EnableDubbo
@SpringBootApplication
@MapperScan("com.sly.fescar.account.mapper")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
