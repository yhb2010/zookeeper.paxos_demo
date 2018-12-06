package com.demo.zookeeper.c5_4curator.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用Curator实现分布式Barrier
public class Recipes_Barrier {
	static String barrier_path = "/curator_recipes_barrier_path";
	static DistributedBarrier barrier;

	public static void main(String[] args) throws Exception {
		//模拟了5个线程
		for (int i = 0; i < 5; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder()
					            .connectString("127.0.0.145:2181")
					            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();

						barrier = new DistributedBarrier(client, barrier_path);
						System.out.println(Thread.currentThread().getName() + "号barrier设置" );
						//setBarrier：完成barrier的设置
						barrier.setBarrier();
						//waitOnBarrier：等待barrier的释放
						barrier.waitOnBarrier();
						System.err.println("启动...");
					} catch (Exception e) {}
				}
			}).start();
		}
		Thread.sleep( 2000 );
		//在主线程中调用removeBarrier方法来释放barrier，同时触发所有等待该barrier的5个线程同时进行各自的业务逻辑。
		barrier.removeBarrier();
	}
}