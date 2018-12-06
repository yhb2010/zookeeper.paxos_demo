package com.demo.zookeeper.c5_4curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//事件监听：cache分为两类：节点监听和子节点监听
public class NodeCache_Sample {

    static String path = "/zk-book12/nodecache";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

	public static void main(String[] args) throws Exception {
		client.start();
		client.create()
		      .creatingParentsIfNeeded()
		      .withMode(CreateMode.EPHEMERAL)
		      .forPath(path, "init".getBytes());
		//NodeCache用于监听指定zookeeper数据节点本身的变化。它不仅可以监听数据节点内容的变化，也能监听指定节点是否存在，
		//如果原本节点不存在，那么Cache就会在节点被创建后触发NodeCacheListener。但是如果该节点被删除，那么就无法触发
		//NodeCacheListener了。
		//构造方法参数说明：
		//client：curator客户端实例
		//path：数据节点的节点路径
		//dataIsCompressed：是否进行数据压缩
	    final NodeCache cache = new NodeCache(client,path,false);
	    //默认是false，如果设置为true，那么NodeCache在第一次启动后就会立刻从zookeeper上读取对应节点的数据内容，并保存在Cache中。
		cache.start(true);
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("Node data update, new data: " +
			    new String(cache.getCurrentData().getData()));
			}
		});
		client.setData().forPath( path, "u".getBytes() );
		Thread.sleep( 1000 );
		client.delete().deletingChildrenIfNeeded().forPath( path );
		Thread.sleep( Integer.MAX_VALUE );
	}
}