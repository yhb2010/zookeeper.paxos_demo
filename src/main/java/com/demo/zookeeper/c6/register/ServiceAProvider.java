package com.demo.zookeeper.c6.register;

import java.net.InetAddress;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class ServiceAProvider {

	private final String PATH = "/configcenter";// 根节点路径
	private final String serviceName = "service-A";
	private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
			.connectString("127.0.0.145:2181")
			.sessionTimeoutMs(5000)
			.retryPolicy(new RetryNTimes(2000,20000))
			.build();

	// 向zookeeper注册服务
	public void init() throws Exception {
		// 註冊當前服務
		InetAddress addr = InetAddress.getLocalHost();
		// String ip= addr.getHostAddress().toString();
		String ip = "192.168.58.130";

		MyConnectionStateListener stateListener = new MyConnectionStateListener(PATH + "/" + serviceName + "/" + ip);
		zkClient.getConnectionStateListenable().addListener(stateListener);

		zkClient.start();

		Stat rootExists = zkClient.checkExists().forPath(PATH);
		// 判断是否存在，不存在则创建服务节点
        if (rootExists == null) {
        	zkClient.create().withMode(CreateMode.PERSISTENT).forPath(PATH);
        }
        Stat serviceExists = zkClient.checkExists().forPath(PATH + "/" + serviceName);
        if(serviceExists == null){
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(PATH + "/" + serviceName);
        }

		// 創建當前服務器節點
		zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(PATH + "/" + serviceName + "/" + ip);

		System.out.println("提供的服务节点名称为：" + PATH + "/" + serviceName + "/" + ip);
	}

	// 提供服务
	public void provide() {

	}

	public static void main(String[] args) throws Exception {
		ServiceAProvider service = new ServiceAProvider();
		service.init();

		Thread.sleep(1000 * 60 * 60 * 24);
	}

}
