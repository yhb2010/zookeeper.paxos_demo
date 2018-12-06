package com.demo.zookeeper.c6.configmanager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZooKeeperFactory {

	public static final String CONNECT_STRING = "127.0.0.145:2181";
	public static final int MAX_RETRIES = 3;
	public static final int BASE_SLEEP_TIMEMS = 3000;
	public static final String NAME_SPACE = "cfg";
	private static CuratorFramework client = null;

	public static CuratorFramework get() throws Exception {
		if(client == null){
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIMEMS, MAX_RETRIES);
			client = CuratorFrameworkFactory.builder()
			.connectString(CONNECT_STRING)
			.retryPolicy(retryPolicy)
			.namespace(NAME_SPACE)
			.build();

			client.start();

			final NodeCache cache = new NodeCache(client, "/properties", false);
			cache.start(true);
			cache.getListenable().addListener(new NodeCacheListener() {
				@Override
				public void nodeChanged() throws Exception {
					System.out.println("Node data update, new data: " +
				    new String(cache.getCurrentData().getData()));
				}
			});
		}
		return client;
	}

}
