package com.demo.zookeeper.c5_4curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//监听指定zookeeper数据节点的子节点变化情况
public class PathChildrenCache_Sample {

    static String path = "/zk-book13";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .sessionTimeoutMs(5000)
            .build();

	public static void main(String[] args) throws Exception {
		client.start();
		//client：curator客户端实例
		//path：数据节点的节点路径
		//dataIsCompressed：是否进行数据压缩
		//cacheData：用于配置是否把节点内容缓存起来，如果配置为true，那么客户端在接收到节点列表变更的同时，也能够获取到节点的数据内容，
		//如果配置为false，则无法获取到节点的数据内容。
		//threadFactory、executorService：可以通过构造一个专门的线程池，来处理事件通知。
		PathChildrenCache cache = new PathChildrenCache(client, path, true);
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			//PathChildrenCacheEvent：定义了所有的事件类型：新增子节点CHILD_ADDED、子节点数据变更CHILD_UPDATED、子节点删除CHILD_REMOVED
			public void childEvent(CuratorFramework client,
					               PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED," + event.getData().getPath());
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_UPDATED," + event.getData().getPath());
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_REMOVED," + event.getData().getPath());
					break;
				default:
					break;
				}
			}
		});
		client.create().withMode(CreateMode.PERSISTENT).forPath(path);
		Thread.sleep( 1000 );
		client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1");
		Thread.sleep( 1000 );
		client.delete().forPath(path+"/c1");
		Thread.sleep( 1000 );
		client.delete().forPath(path);
		Thread.sleep(Integer.MAX_VALUE);
		//curator无法实现对二级子节点的事件监听，也就是说，如果使用PathChildrenCache对/zk-book进行监听，那么当/zk-book/c1/c2
		//节点被创建或删除时，是无法触发子节点变更事件的。
	}
}