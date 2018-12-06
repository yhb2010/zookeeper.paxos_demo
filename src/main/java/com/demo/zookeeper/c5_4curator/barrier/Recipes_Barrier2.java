package com.demo.zookeeper.c5_4curator.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Recipes_Barrier2 {
	static String barrier_path = "/curator_recipes_barrier_path";
	public static void main(String[] args) throws Exception {

		for (int i = 0; i < 5; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder()
					            .connectString("127.0.0.145:2181")
					            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();

						DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, barrier_path,5);
						Thread.sleep( Math.round(Math.random() * 3000) );
						System.out.println(Thread.currentThread().getName() + "号进入barrier" );
						//每个barrier的参与者会在调用enter方法之后进行等待，此时处于准备进入状态。一旦准备进入的成员达到5个以后，所有的成员会被同时触发进入。
						barrier.enter();
						System.out.println("启动...");
						Thread.sleep( Math.round(Math.random() * 3000) );
						//之后调用leave方法会再次进入等待，此时处于准备退出状态。一旦准备退出的barrier成员达到5个后，所有的成员同样会被同时触发退出。
						barrier.leave();
						System.out.println( "退出..." );
					} catch (Exception e) {}
				}
			}).start();
		}
	}
}