package com.demo.zookeeper.c5_3;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

//创建连接 -> 创建一个最基本的ZooKeeper对象实例，复用sessionId和sessionPasswd
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        connectedSemaphore.await();
        long sessionId = zookeeper.getSessionId();
        byte[] passwd  = zookeeper.getSessionPasswd();

        //使用了错误的sessionId和sessionPasswd
        //使用了错误的sessionId和sessionPasswd
        zookeeper = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				1l,//
				"test".getBytes());
        //使用了正确的sessionId和sessionPasswd
        zookeeper = new ZooKeeper("127.0.0.145:2181",
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				sessionId,//
				passwd);
        Thread.sleep( Integer.MAX_VALUE );
    }

    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }

}