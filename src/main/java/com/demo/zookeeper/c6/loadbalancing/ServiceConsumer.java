package com.demo.zookeeper.c6.loadbalancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

//消费者类
public class ServiceConsumer {

	private List<String> serverList = new ArrayList<String>();
	private final String serviceName = "service-A";
	private final String SERVICE_PATH = "/configcenter/"+serviceName;//服务节点路径
	private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
			.connectString("127.0.0.145:2181")
			.sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	// 初始化服务地址信息
	public void init() throws Exception {
		zkClient.start();

		Stat rootExists = zkClient.checkExists().forPath(SERVICE_PATH);
		if (rootExists != null) {
			serverList = zkClient.getChildren().forPath(SERVICE_PATH);
		} else {
			throw new RuntimeException("service not exist!");
		}

		// 注册事件监听
		PathChildrenCache cache = new PathChildrenCache(zkClient, SERVICE_PATH, true);
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			//PathChildrenCacheEvent：定义了所有的事件类型：新增子节点CHILD_ADDED、子节点数据变更CHILD_UPDATED、子节点删除CHILD_REMOVED
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
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

				//重新获取服务列表
				serverList = zkClient.getChildren().forPath(SERVICE_PATH);
				consume();
			}
		});
	}

	// 消费服务
	public void consume() {
		// 通过负责均衡算法，得到一台服务器进行调用
		int index = getRandomNum(0, serverList.size() - 1);
		System.out.println("调用" + serverList.get(index) + "提供的服务：" + serviceName);
	}

	public int getRandomNum(int min, int max) {
		Random rdm = new Random();
		return rdm.nextInt(max - min + 1) + min;
	}

	public static void main(String[] args) throws Exception {
		ServiceConsumer consumer = new ServiceConsumer();

		consumer.init();
		consumer.consume();

		Thread.sleep(1000 * 60 * 60 * 24);
	}

}
