package com.demo.zookeeper.c5_3.getchildren;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

// ZooKeeper API 获取节点数据内容，使用同步(sync)接口。
public class GetData_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {

    	String path = "/zk-book3";
    	zk = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new GetData_API_Sync_Usage());
        connectedSemaphore.await();
        zk.create( path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );

        //调用getData时注册监听，监听NodeDataChanged事件
        System.out.println(new String(zk.getData( path, true, stat )));
        System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());

        zk.setData( path, "123".getBytes(), -1 );

        Thread.sleep( Integer.MAX_VALUE );
    }

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeDataChanged) {
				try {
					System.out.println(new String(zk.getData(event.getPath(), true, stat)));
					System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 *  123
		47244642233,47244642233,0
		123
		47244642233,47244642234,1
		我们要明确一个概念：节点的数据内容或是节点的数据版本变化，都被看做是zookeeper节点的变化，明白这个概念后，再回过来看上面的
		结果输出，可以看出，该节点的zxid为47244642233时被创建，在zxid为47244642234时被更新，于是节点的数据版本从0变化到1，
		数据内容或版本的变化都会触发服务端的NodeDataChanged通知。
	 * */

}