package com.demo.zookeeper.c5_4curator.queue;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedDelayQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;

//任务生产者
//任务生产者主要有2个逻辑，一个是在构造代码块中初始化curator的延时任务队列，另一个是提供一个produce方法供外部往队列里放延时任务。
public class DelayTaskProducer {

    private static final String CONNECT_ADDRESS="ip104:2181";

    private static final int SESSION_OUTTIME = 5000;

    private static final String NAMESPACE = "delayTask";

    private static final String QUEUE_PATH = "/queue";

    private static final String LOCK_PATH = "/lock";

    private CuratorFramework curatorFramework;

    private DistributedDelayQueue<String> delayQueue;

    {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        curatorFramework= CuratorFrameworkFactory.builder().connectString(CONNECT_ADDRESS)
                .sessionTimeoutMs(SESSION_OUTTIME).retryPolicy(retryPolicy)
                .namespace(NAMESPACE).build();
        curatorFramework.start();
        //在初始化延时任务时，需要传入一个字节数组与任务描述实体之间的序列化器，这里简单地将任务描述处理成字符串
        delayQueue= QueueBuilder.builder(curatorFramework, new DelayTaskConsumer(), new DelayTaskSerializer(), QUEUE_PATH).lockPath(LOCK_PATH).buildDelayQueue();
        try {
            delayQueue.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void produce(String id,long timeStamp){
        try {
            delayQueue.put(id,timeStamp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}