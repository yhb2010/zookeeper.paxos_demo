package com.demo.zookeeper.c5_3.create;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

//ZooKeeper API创建节点，使用同步(sync)接口。
public class ZooKeeper_Create_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_Create_API_Sync_Usage());
        connectedSemaphore.await();

        //path：需要创建的数据节点的节点路径
        //data[]：一个字节数组，是节点创建后的初始内容。
        //如果是字符串，简单使用Stirng.getBytes()来生成字节数组，对于复杂对象，使用Hessian或是kryo等专门的序列化工具来进行序列化。
        //acl：节点的acl策略
        //createMode：节点类型，是一个枚举，有4种类型：1、持久persistent，2、持久顺序persistent_sequential
        //3、临时ephemeral，4、临时顺序ephemeral_sequential
        //cb：异步回调函数，需要实现StringCallback接口，当服务端创建完节点后，zookeeper客户端就会调用这个方法，这样就可以处理相关的业务逻辑。
        //ctx：用于传递一个对象，可以在回调方法执行的时候使用，通常是放一个上下文信息
        String path1 = zookeeper.create("/zk-test-ephemeral-",
        		"".getBytes(),
        		Ids.OPEN_ACL_UNSAFE,
        		CreateMode.EPHEMERAL);
        //创建临时节点，返回的是当时传入的path参数
        System.out.println("Success create znode: " + path1);

        String path2 = zookeeper.create("/zk-test-ephemeral-",
        		"".getBytes(),
        		Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        //创建临时顺序节点，zookeeper会自动在节点后缀加上一个数字，返回的是该数据节点的一个完整的节点路径
        System.out.println("Success create znode: " + path2);

        //存入一个图片
        InputStream fis = ZooKeeper_Create_API_Sync_Usage.class.getResourceAsStream("catchpic.jpg");
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        String path3 = zookeeper.create("/zk-testpic-ephemeral-",
        		data,
        		Ids.OPEN_ACL_UNSAFE,
        		CreateMode.EPHEMERAL);
        System.out.println("Success create znode: " + path3);

        Thread.sleep(Integer.MAX_VALUE);
    }

    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }

}
