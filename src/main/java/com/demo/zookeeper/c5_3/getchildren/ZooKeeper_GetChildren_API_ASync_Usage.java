package com.demo.zookeeper.c5_3.getchildren;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

//ZooKeeper API 获取子节点列表，使用异步(ASync)接口。
public class ZooKeeper_GetChildren_API_ASync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;

    public static void main(String[] args) throws Exception{
    	String path = "/zk-book1";
        zk = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_GetChildren_API_ASync_Usage());
        connectedSemaphore.await();
        zk.create(path, "".getBytes(),
        		  Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create(path+"/c1", "".getBytes(),
        		  Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        //将子节点列表的获取逻辑进行了异步化，可以使用在这种场景下：应用启动的时候，会获取一些配置信息，例如机器列表，
        //这些配置通常比较大，并且不希望配置的获取影响应用的主流程。
        zk.getChildren(path, true, new IChildren2Callback(), null);

        zk.create(path+"/c2", "".getBytes(),
      		  Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep( Integer.MAX_VALUE );
    }

    @Override
	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeChildrenChanged) {
				try {
					System.out.println("ReGet Child:" + zk.getChildren(event.getPath(), true));
				} catch (Exception e) {
				}
			}
		}
	}
}

class IChildren2Callback implements AsyncCallback.Children2Callback{
	public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        System.out.println("Get Children znode result: [response code: " + rc + ", param path: " + path
                + ", ctx: " + ctx + ", children list: " + children + ", stat: " + stat);
    }
}