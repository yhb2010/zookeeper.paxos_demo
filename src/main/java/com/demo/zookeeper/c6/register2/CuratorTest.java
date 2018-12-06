package com.demo.zookeeper.c6.register2;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

public class CuratorTest {

	private CuratorFramework zkTools;
	private ConcurrentSkipListSet watchers = new ConcurrentSkipListSet();
	private static Charset charset = Charset.forName("utf-8");
	public enum ZookeeperWatcherType {
		GET_DATA, GET_CHILDREN, EXITS, CREATE_ON_NO_EXITS
	}

	public CuratorTest() {
		zkTools = CuratorFrameworkFactory.builder()
				.connectString("127.0.0.145:2181").namespace("zk/test")
				.retryPolicy(new RetryNTimes(2000, 20000)).build();
		zkTools.start();
	}

	public void addReconnectionWatcher(final String path, final ZookeeperWatcherType watcherType, final ZKWatchRegister watcher) {
		synchronized (this) {
			if (!watchers.contains(watcher.toString()))// 不要添加重复的监听事件
			{
				watchers.add(watcher.toString());
				System.out.println("add new watcher " + watcher);
				zkTools.getConnectionStateListenable().addListener(
						new ConnectionStateListener() {
							@Override
							public void stateChanged(CuratorFramework client, ConnectionState newState) {
								System.out.println(newState);
								if (newState == ConnectionState.LOST) {// 处理session过期
									try {
										if (watcherType == ZookeeperWatcherType.EXITS) {
											zkTools.checkExists()
													.usingWatcher(watcher)
													.forPath(path);
										} else if (watcherType == ZookeeperWatcherType.GET_CHILDREN) {
											zkTools.getChildren()
													.usingWatcher(watcher)
													.forPath(path);
										} else if (watcherType == ZookeeperWatcherType.GET_DATA) {
											zkTools.getData()
													.usingWatcher(watcher)
													.forPath(path);
										} else if (watcherType == ZookeeperWatcherType.CREATE_ON_NO_EXITS) {
											// ephemeral类型的节点session过期了，需要重新创建节点，并且注册监听事件，之后监听事件中，
											// 会处理create事件，将路径值恢复到先前状态
											Stat stat = zkTools.checkExists()
													.usingWatcher(watcher)
													.forPath(path);
											if (stat == null) {
												System.err.println("to create");
												zkTools.create()
														.creatingParentsIfNeeded()
														.withMode(CreateMode.EPHEMERAL)
														.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
														.forPath(path, watcher.getValue());
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
			}
		}
	}

	public void register() throws Exception {
		String ip = InetAddress.getLocalHost().getHostAddress();
		String registeNode = "/zk/register/" + ip;// 节点路径

		byte[] data = "disable".getBytes(charset);// 节点值

		//创建一个register watcher
		ZKWatchRegister watcher = new ZKWatchRegister(zkTools, registeNode, data);

		Stat stat = zkTools.checkExists().forPath(registeNode);
		if (stat != null) {
			zkTools.delete().forPath(registeNode);
		}
		zkTools.create().creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
				.forPath(registeNode, data);// 创建的路径和值

		// 添加到session过期监控事件中
		addReconnectionWatcher(registeNode, ZookeeperWatcherType.CREATE_ON_NO_EXITS, watcher);
		data = zkTools.getData().usingWatcher(watcher).forPath(registeNode);
		System.out.println("get path form zk : " + registeNode + ":" + new String(data, charset));
	}

	public static void main(String[] args) throws Exception {
		CuratorTest test = new CuratorTest();
		test.register();
		Thread.sleep(10000000000L);
	}

}
