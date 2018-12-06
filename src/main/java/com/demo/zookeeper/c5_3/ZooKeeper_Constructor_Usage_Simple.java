package com.demo.zookeeper.c5_3;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

//创建连接 -> 创建一个最基本的ZooKeeper对象实例
//创建过程是异步的，调用完构造方法后，并没有建立起一个可用的连接，会话的生命周期处于connecting状态。
//当该会话真正创建完毕后，zookeeper服务器会向对话对应的客户端发送一个事件通知，已告知客户端，客户端只有在获取这个通知后，才真正建立了会话
public class ZooKeeper_Constructor_Usage_Simple implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{

    	//服务器列表，英文状态的都逗号分开的host:port字符串组成，每一个都代表一台zookeeper机器，
    	//例如127.0.0.145:2181,127.0.0.145:2182,127.0.0.145:2183，这样就是指定了三台机器。
    	//还可以指定根目录：127.0.0.145:2181,127.0.0.145:2182,127.0.0.145:2183/zk-book，这样所有的后续操作都在这个目录下。
        ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181",
        		//会话超时时间，以毫秒为单位。在一个会话周期内，客户端与服务器端通过心跳检测机制来维持会话的有效性，一旦在sessionTimeout时间内没有进行有效的心跳检测，会话就会失效。
        									5000,
        									//运行传入接口Watcher的实现类对象来作为默认的Watcher事件通知处理器，为null表示不需要默认的Watcher处理器。
        									new ZooKeeper_Constructor_Usage_Simple()
        //canBeReadOnly：
        //这是一个boolean参数，用于标识当前会话是否支持read-only模式，一般情况下，一个机器如果和集群中过半数的机器失去网络连接，那么该机器
        //不再处理客户端请求，但是在某些时候，还是希望它可以提供读服务，这就是read-only模式。
        //sessionId和sessionPAsswd
        //代表会话id和会话密钥，这两个参数可以确定唯一一个会话，同时客户端使用这两个参数可以实现客户端会话的复用，从而达到恢复会话的效果。
        //具体使用方法是，第一次连接上zookeeper服务器后，调用zookeeper对象的getSessionId和getSessionPasswd获取到这两个参数值后，
        //就可以在下次创建zookeeper对象实例的时候传入构造方法了。
        );
        System.out.println(zookeeper.getState());
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {}
        System.out.println("ZooKeeper session established.");
    }

    //处理zookeeper服务器的Watch通知，在收到服务端发来的SyncConnected事件后，解除主程序在CountDownLatch上的等待阻塞，至此，客户端会话创建完毕。
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}