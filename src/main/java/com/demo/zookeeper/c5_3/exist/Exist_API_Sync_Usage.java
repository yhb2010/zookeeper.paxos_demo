package com.demo.zookeeper.c5_3.exist;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

// ZooKeeper API 删除节点，使用同步(sync)接口。
public class Exist_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;
    public static void main(String[] args) throws Exception {

    	String path = "/zk-book7";
    	zk = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new Exist_API_Sync_Usage());
    	connectedSemaphore.await();

    	//通过exists检查是否存在指定节点，同时注册一个watcher
    	//watcher：注册watcher，可以监听三类事件：1、节点被创建、2、节点被删除、3、节点被更新
    	//对于子节点的变化不会通知客户端
    	//无论指定节点是否存在，通过调用exists接口都可以注册watcher
    	//exists接口中注册的watcher，能够对节点创建、节点删除、节点数据更新事件进行监听
    	zk.exists( path, true );

    	zk.create( path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );

    	zk.setData( path, "123".getBytes(), -1 );

    	zk.create( path+"/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );

    	zk.delete( path+"/c1", -1 );

    	zk.delete( path, -1 );

        Thread.sleep( Integer.MAX_VALUE );
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if (KeeperState.SyncConnected == event.getState()) {
                if (EventType.None == event.getType() && null == event.getPath()) {
                    connectedSemaphore.countDown();
                } else if (EventType.NodeCreated == event.getType()) {
                    System.out.println("Node(" + event.getPath() + ")Created");
                    zk.exists( event.getPath(), true );
                } else if (EventType.NodeDeleted == event.getType()) {
                    System.out.println("Node(" + event.getPath() + ")Deleted");
                    zk.exists( event.getPath(), true );
                } else if (EventType.NodeDataChanged == event.getType()) {
                    System.out.println("Node(" + event.getPath() + ")DataChanged");
                    zk.exists( event.getPath(), true );
                }
            }
        } catch (Exception e) {}
    }
}