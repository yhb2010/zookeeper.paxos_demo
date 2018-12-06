package com.demo.zookeeper.c6.configmanager2;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Startup {

	public static void main(String[] args) throws InterruptedException {
		new ClassPathXmlApplicationContext("classpath:/config/applicationContext2.xml");
		Thread.sleep(Integer.MAX_VALUE);
	}

}
