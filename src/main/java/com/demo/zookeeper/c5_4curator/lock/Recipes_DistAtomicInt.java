package com.demo.zookeeper.c5_4curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

// 使用Curator实现分布式计数器
//利用分布式锁实现：指定一个zookeeper数据节点作为计数器，多个应用实例在分布式锁的控制下，通过更新该数据节点的内容来实现技术功能。
public class Recipes_DistAtomicInt {

	static String distatomicint_path = "/curator_recipes_distatomicint_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

	public static void main( String[] args ) throws Exception {
		client.start();
		DistributedAtomicInteger atomicInteger =
		new DistributedAtomicInteger( client, distatomicint_path,
									new RetryNTimes( 3, 1000 ) );
		AtomicValue<Integer> rc = atomicInteger.add( 8 );
		System.out.println( "Result: " + rc.postValue() );
	}
}