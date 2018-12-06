package com.demo.zookeeper.c5_4curator.zkpath;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;
import org.apache.zookeeper.ZooKeeper;

//ZKPaths提供了简单的api用于创建znode路径、递归创建和删除节点等。
public class ZKPaths_Sample {

	static String path = "/curator_zkpath_sample";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString( "127.0.0.145:2181" )
			.sessionTimeoutMs( 5000 )
			.retryPolicy( new ExponentialBackoffRetry( 1000, 3 ) )
			.build();

	public static void main(String[] args) throws Exception {
		client.start();
		ZooKeeper zookeeper = client.getZookeeperClient().getZooKeeper();

		System.out.println(ZKPaths.fixForNamespace(path, "/sub"));
		//makePath: 根据给定的path和子节点名, 创建一个完整path
		System.out.println(ZKPaths.makePath(path, "sub"));
		System.out.println( ZKPaths.getNodeFromPath( "/curator_zkpath_sample/sub1" ) );

		PathAndNode pn = ZKPaths.getPathAndNode( "/curator_zkpath_sample/sub1" );
		System.out.println(pn.getPath());
		System.out.println(pn.getNode());

		String dir1 = path + "/child1";
		String dir2 = path + "/child2";
		//mkdirs: 根据给定路径递归创建所有node
		ZKPaths.mkdirs(zookeeper, dir1);
		ZKPaths.mkdirs(zookeeper, dir2);
		//getSortedChildren: 根据给定路径, 返回一个按序列号排序的子节点列表
		System.out.println(ZKPaths.getSortedChildren( zookeeper, path ));

		ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(), path, true);
	}
}