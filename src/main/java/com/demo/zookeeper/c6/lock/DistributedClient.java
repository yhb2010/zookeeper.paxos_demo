package com.demo.zookeeper.c6.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class DistributedClient {

	private static final int SESSION_TIMEOUT = 5000;
	private String hosts = "127.0.0.145:2181";
	private String groupNode = "locks";
	private String subNode = "sub";
	private ZooKeeper zk;
	// 当前client创建的子节点
	private volatile String thisPath;
	// 当前client等待的子节点
	private volatile String waitPath;
	private CountDownLatch latch = new CountDownLatch(1);

	/**
	 * 连接zookeeper
	 *
	 * @param countDownLatch
	 */
	public void connectZookeeper(final CountDownLatch countDownLatch)
			throws Exception {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					if (event.getState() == KeeperState.SyncConnected) {
						latch.countDown();
					}

					// 发生了waitPath的删除事件
					/**
					 * 假设某个client在获得锁之前挂掉了, 由于client创建的节点是ephemeral类型的,
					 * 因此这个节点也会被删除, 从而导致排在这个client之后的client提前获得了锁.
					 * 此时会存在多个client同时访问共享资源. 如何解决这个问题呢? 可以在接到删除通知的时候, 进行一次确认,
					 * 确认当前的thisPath是否真的是列表中最小的节点.
					 */
					if (event.getType() == EventType.NodeDeleted && event.getPath().equals(waitPath)) {
						// 确认thisPath是否真的是列表中的最小节点
						List<String> childrenNodes = zk.getChildren("/" + groupNode, false);
						String thisNode = thisPath.substring(("/" + groupNode + "/").length());
						// 排序
						Collections.sort(childrenNodes);
						int index = childrenNodes.indexOf(thisNode);
						if (index == 0) {
							// 确实是最小节点
							doSomething(countDownLatch);
						} else {
							// 说明waitPath是由于出现异常而挂掉的
							// 更新waitPath
							waitPath = "/" + groupNode + "/" + childrenNodes.get(index - 1);
							// 重新注册监听, 并判断此时waitPath是否已删除
							if (zk.exists(waitPath, true) == null) {
								doSomething(countDownLatch);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 等待连接建立
		latch.await();
		if (null != zk) {
			Stat stat = zk.exists("/" + groupNode, false);
			if (null == stat) {
				zk.create("/" + groupNode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		}
		// 创建子节点
		thisPath = zk.create("/" + groupNode + "/" + subNode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		Thread.sleep(10);
		List<String> childrenNodes = zk.getChildren("/" + groupNode, false);
		// 列表中只有一个子节点, 那肯定就是thisPath, 说明client获得锁
		if (childrenNodes.size() == 1) {
			doSomething(countDownLatch);
		} else {
			String thisNode = thisPath.substring(("/" + groupNode + "/").length());
			Collections.sort(childrenNodes);
			int index = childrenNodes.indexOf(thisNode);
			if (index == 0) {
				// index == 0, 说明thisNode在列表中最小, 当前client获得锁
				doSomething(countDownLatch);
			} else {
				// 获得排名比thisPath前1位的节点
				this.waitPath = "/" + groupNode + "/" + childrenNodes.get(index - 1);
				// 在waitPath上注册监听器, 当waitPath被删除时, zookeeper会回调监听器的process方法
				zk.getData(waitPath, true, new Stat());
			}
		}
	}

	private void doSomething(CountDownLatch countDownLatch) throws Exception {
		try {
			System.out.println("当前线程:" + Thread.currentThread().getName() + "获得了锁:" + thisPath);
			// 模拟获得锁的线程执行相应逻辑
			Thread.sleep(2000);
		} finally {
			System.out.println("当前线程:" + Thread.currentThread().getName() + "已经释放了锁,让其它客户端有机会去获取," + thisPath);
			// 将thisPath删除, 监听thisPath的client将获得通知,相当于释放锁
			zk.delete(this.thisPath, -1);
			countDownLatch.countDown();
			zk.close();
		}
	}

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		final CountDownLatch latch = new CountDownLatch(20);
		for (int i = 0; i < 20; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						DistributedClient lock = new DistributedClient();
						lock.connectZookeeper(latch);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (null != executorService) {
				executorService.shutdown();
			}
		}
	}

}
