package com.demo.zookeeper.c5_4curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class PathChildrenCache_Sample_NoCacheData {

    static String path = "/zk-book14";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .sessionTimeoutMs(5000)
            .build();

	public static void main(String[] args) throws Exception {
		client.start();
		PathChildrenCache cache = new PathChildrenCache(client, path, true);
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client,
					               PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				//获取节点的数据内容
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED," + new String(event.getData().getData()));
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_UPDATED," + new String(event.getData().getData()));
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_REMOVED," + new String(event.getData().getData()));
					break;
				default:
					break;
				}
			}
		});
		client.create().withMode(CreateMode.PERSISTENT).forPath(path, "init".getBytes());
		Thread.sleep( 1000 );
		client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1", "init2".getBytes());
		Thread.sleep( 1000 );
		client.delete().forPath(path+"/c1");
		Thread.sleep( 1000 );
		client.delete().forPath(path);
		Thread.sleep(Integer.MAX_VALUE);
	}
}