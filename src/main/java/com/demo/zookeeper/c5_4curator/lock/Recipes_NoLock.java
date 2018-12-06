package com.demo.zookeeper.c5_4curator.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁，这个没加锁：
 * 生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
生成的订单号是 : 15:24:55|200
都是一个号，并发引起的。
 * @author dell
 *
 */
public class Recipes_NoLock {

	public static void main(String[] args) throws Exception {
		final CountDownLatch down = new CountDownLatch(1);
		for(int i = 0; i < 10; i++){
			new Thread(new Runnable() {
				public void run() {
					try {
						down.await();
					} catch ( Exception e ) {
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.err.println("生成的订单号是 : "+orderNo);
				}
			}).start();
		}
		down.countDown();
	}
}