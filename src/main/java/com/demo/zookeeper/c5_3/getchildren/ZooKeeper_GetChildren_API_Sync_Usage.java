package com.demo.zookeeper.c5_3.getchildren;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

// ZooKeeper API 获取子节点列表，使用同步(sync)接口。
public class ZooKeeper_GetChildren_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;

    public static void main(String[] args) throws Exception{

    	String path = "/zk-book";
        zk = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_GetChildren_API_Sync_Usage());
        connectedSemaphore.await();
        zk.create(path, "".getBytes(),
        		  Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create(path+"/c1", "".getBytes(),
        		  Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        //调用getChildren的同步接口来获取所有子节点，同时注册了一个watcher
        //path：指定数据节点的节点路径，即api调用的目的地是获取该节点的子节点列表
        //watcher：注册的Watcher，一旦在本次子节点获取之后，子节点列表发生变更的话，那么就会向客户端发送通知，该参数运行传入null
        //watch：表明是否需要注册一个watcher，在创建zookeeper连接时，我们可以传入一个默认的watcher，这里就要使用该默认watcher了，
        //如果这个参数是true，那么zookeeper客户端会自动使用该默认watch，如果是false，表明不需要注册watcher。
        //cb：异步回调函数
        //ctx：传递上下文信息的对象
        //stat：指定数据节点的节点状态信息，用法是在接口中传入一个旧的stat变量，该stat变量会在方法执行过程中，被来自服务端响应的新stat对象替换。
        //stat对象里包含了节点创建时的事务id，最后一次修改的事务id和节点数据内容长度等。
        List<String> childrenList = zk.getChildren(path, true);
        System.out.println(childrenList);

        zk.create(path+"/c2", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep( Integer.MAX_VALUE );
    }

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeChildrenChanged) {
				try {
					//列表变化时，会触发NodeChildrenChanged事件，客户端必须自己获取最新的子节点列表
					//watch通知是一次性的，一旦触发通知后，该watcher就失效了，因此客户端需要反复注册watcher。
					System.out.println("ReGet Child:" + zk.getChildren(event.getPath(), true));
				} catch (Exception e) {
				}
			}
		}
	}

}
