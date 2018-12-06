package com.demo.zookeeper.c5_4curator.create;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用curator来创建一个含隔离命名空间的ZooKeeper客户端，也就是指定zookeeper的根路径
public class Create_Session_Sample_With_Namespace {
    public static void main(String[] args) throws Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
        CuratorFrameworkFactory.builder()
                             .connectString("127.0.0.145:2181")
                             .sessionTimeoutMs(5000)
                             .retryPolicy(retryPolicy)
                             .namespace("base")
                             .build();
        client.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}