package com.demo.zookeeper.c6.configmanager;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Startup {

	public static void main(String[] args) throws InterruptedException {
		new ClassPathXmlApplicationContext("classpath:/config/applicationContext.xml");
		Thread.sleep(Integer.MAX_VALUE);
	}

}
