package com.ssafy.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(HelloApplication.class, args);
		for(String beanName: ctx.getBeanDefinitionNames()) System.out.println(beanName);
	}

}
