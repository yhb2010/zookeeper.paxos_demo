package com.demo.zookeeper.c5_4curator.create;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//使用Curator的异步接口
public class Create_Node_Background_Sample {

    static String path = "/zk-book8";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static CountDownLatch semaphore = new CountDownLatch(2);
    static ExecutorService tp = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
    	client.start();
        System.out.println("Main thread: " + Thread.currentThread().getName());
        // 此处传入了自定义的Executor
        //EventThread的串行处理机制在绝大部分应用场景中能够保证对事件处理的顺序性，但这个特性也有其弊端，就是一旦碰上一个复杂的处理单元，
        //就会消耗过长的处理时间，从而影响对其他事件的处理，因此，在上面的inBackground接口中，允许用户传入一个Executor实例，这样一来，就可以
        //把那些比较复杂的事件处理放到一个专门的线程池中去，如Executors.newFixedThreadPool(2)。
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
        	//引入了BackgroundCallback接口，用来处理异步接口调用之后服务端返回的结果信息，该接口只有一个processResult方法，参数：
        	//client：当前客户端实例
        	//event：服务端事件，比较重要的是事件类型和事件码：
        	//事件类型：
        	//getType()，代表本次事件的类型，主要有create、delete、exists、get_data、set_data、children、sync、get_acl、watched、closing（客户端与服务端连接断开事件）
        	//响应码：定义在org.apach.zookeeper.KeeperException.Code类中，常见的有0（ok）、-4（ConnectionLoss）、-110（NodeExists）、-112（SessionExpired）等，分别代表接口调用成功、客户端与服务端连接已断开、指定节点已存在、会话已过期
        	@Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                System.out.println("Thread of processResult: " + Thread.currentThread().getName());
                semaphore.countDown();
            }
        }, tp).forPath(path, "init".getBytes());

        // 此处没有传入自定义的Executor
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                System.out.println("Thread of processResult: " + Thread.currentThread().getName());
                semaphore.countDown();
            }
        }).forPath(path, "init".getBytes());

        semaphore.await();
        tp.shutdown();
    }
}