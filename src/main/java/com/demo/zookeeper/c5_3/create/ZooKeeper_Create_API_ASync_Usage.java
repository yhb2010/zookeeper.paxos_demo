package com.demo.zookeeper.c5_3.create;

import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

// ZooKeeper API创建节点，使用异步(async)接口。
public class ZooKeeper_Create_API_ASync_Usage implements Watcher {

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {

		// 带命名空间
		// ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181/apps/X",
		ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181", 5000, //
				new ZooKeeper_Create_API_ASync_Usage());
		connectedSemaphore.await();

		zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				new IStringCallback(), "I am context.");

		zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				new IStringCallback(), "I am context.");

		zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
				new IStringCallback(), "I am context.");
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}
}

// AsyncCallback包含了StatCallback、DataCallback、ACLCallback、ChildrenCallback、Children2Callback、StringCallback、VoidCallback
// 七种不同的回调接口，用户可以在不同的异步接口中实现不同的接口
// 在异步接口中，接口本身不抛出异常，所有异常都在回调函数中通过rc来体现
class IStringCallback implements AsyncCallback.StringCallback {
	// rc类型：1、0接口调用成功，2、-4客户端与服务端连接已断开，3、-110指定节点已存在，4、-112会话已过期
	// path：接口调用时传入api的数据节点的节点路径参数值
	// ctx：接口调用时传入api的ctx参数值
	// name：实际在服务端创建的节点名。上面代码中，第三次创建时，由于创建的节点类型是顺序节点，因此在服务端没有真正创建好顺序节点
	// 之前，客户端无法知道节点的完整节点路径，于是，在回调方法中，服务端会返回这个数据节点的完整节点路径
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("Create path result: [" + rc + ", " + path + ", "
				+ ctx + ", real path name: " + name);
	}

}