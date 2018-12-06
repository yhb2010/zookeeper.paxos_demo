package com.demo.zookeeper.c5_3.setdata;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

// ZooKeeper API 更新节点数据内容，使用同步(sync)接口。
public class SetData_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;

    public static void main(String[] args) throws Exception {

    	String path = "/zk-book5";
    	zk = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new SetData_API_Sync_Usage());
    	connectedSemaphore.await();

        zk.create( path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
        zk.getData( path, true, null );

        //version传入-1，说明客户端需要基于数据的最新版本进行更新操作。如果对zookeeper数据节点的更新操作没有原子性要求，就可以使用-1
        //返回的stat里带有最新的version
        Stat stat = zk.setData( path, "456".getBytes(), -1 );
        System.out.println(stat.getCzxid()+","+
			        	   stat.getMzxid()+","+
			        	   stat.getVersion());
        Stat stat2 = zk.setData( path, "456".getBytes(), stat.getVersion() );
        System.out.println(stat2.getCzxid()+","+
	        	   		   stat2.getMzxid()+","+
	        	   		   stat2.getVersion());
        try {
			zk.setData( path, "456".getBytes(), stat.getVersion() );
		} catch ( KeeperException e ) {
			System.out.println("Error: " + e.code() + "," + e.getMessage());
		}
        Thread.sleep( Integer.MAX_VALUE );
    }

    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            if (EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}