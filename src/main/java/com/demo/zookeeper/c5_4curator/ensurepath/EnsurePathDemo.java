package com.demo.zookeeper.c5_4curator.ensurepath;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

//EnsurePath：提供一种能够确保数据节点存在的机制，多用于这样的业务场景：
//上层业务希望对一个数据节点进行一些操作，但是操作之前需要确保该节点存在。
//EnsurePath：如果节点已经存在，那么就不进行任何操作，也不对外抛出异常，如果不存在则创建数据节点。
public class EnsurePathDemo {

    static String path = "/zk-book";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

	public static void main(String[] args) throws Exception {

		client.start();
		client.usingNamespace( "zk-book" );

		EnsurePath ensurePath = new EnsurePath(path);
		ensurePath.ensure(client.getZookeeperClient());
		ensurePath.ensure(client.getZookeeperClient());

		EnsurePath ensurePath2 = client.newNamespaceAwareEnsurePath(path + "/c1");
		ensurePath2.ensure(client.getZookeeperClient());

		EnsurePath ensurePath3 = client.newNamespaceAwareEnsurePath(path + "/c2");
		ensurePath3.ensure(client.getZookeeperClient());
	}
}