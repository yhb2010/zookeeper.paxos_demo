package com.demo.zookeeper.c5_4curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

//TreeNodeCache将NodeCache和PathChildrenCache功能结合到一起了。他不仅可以对子节点和父节点同时进行监听。
public class TreeNodeCache {

	public static void main(String[] args) throws Exception {
		TestingServer server = null;
		CuratorFramework client = null;
		NodeCache nodeCache = null;
		String path = "/francis/nodecache/b";

		try {
			server = new TestingServer();
			client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();

			TreeCache treeNodeCache = new TreeCache(client, path);
			treeNodeCache.start();
			treeNodeCache.getListenable().addListener(new TreeCacheListener() {

				@Override
				public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
					switch (event.getType()) {
					case NODE_ADDED:
						System.out.println("added:" + event.getData().getPath());
						break;
					case NODE_UPDATED:
						System.out.println("updated:" + event.getData().getPath());
						break;
					case NODE_REMOVED:
						System.out.println("removed:" + event.getData().getPath());
						break;
					default:
						System.out.println("other:" + event.getType());
					}
				}

			});

			// 创建父节点
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT)
					.forPath(path, "init".getBytes());
			Thread.sleep(1000);

			// 创建子节点
			String childPath1 = ZKPaths.makePath(path, "a");
			childPath1 = client.create().withMode(CreateMode.PERSISTENT)
					.forPath(childPath1, "1".getBytes());
			Thread.sleep(1000);

			// 对子节点赋值
			client.setData().forPath(childPath1, "aaa".getBytes());
			Thread.sleep(1000);

			// 对子节点赋值
			client.setData().forPath(path, "aaa".getBytes());
			Thread.sleep(1000);

			// 删除子节点
			client.delete().forPath(childPath1);
			client.delete().deletingChildrenIfNeeded().forPath("/francis");

			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 这里因为是测试，没有加他们。
			CloseableUtils.closeQuietly(nodeCache);
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(server);
		}
		//other:INITIALIZED
		//added:/francis/nodecache/b
		//added:/francis/nodecache/b/a
		//updated:/francis/nodecache/b/a
		//updated:/francis/nodecache/b
		//removed:/francis/nodecache/b/a
		//removed:/francis/nodecache/b
	}

}
