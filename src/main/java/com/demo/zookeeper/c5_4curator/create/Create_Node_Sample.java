package com.demo.zookeeper.c5_4curator.create;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//使用Curator创建节点
public class Create_Node_Sample {
    static String path = "/zk-book6/c1";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        client.create()
              .creatingParentsIfNeeded()
              .withMode(CreateMode.EPHEMERAL)
              .forPath(path, "init".getBytes());
        //创建一个节点，初始内容为空，默认是持久节点，默认内容为空
        //client.create().forPath(path);
        //创建一个节点，附带初始内容
        //client.create().forPath(path, "init".getBytes())
        //创建一个临时节点，初始内容为空
        //client.create().withMode(CreateMode.EPHEMERAL).forPath(path)
        //创建一个临时节点，并自动递归创建父节点
        //client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path)
        //由于zookeeper规定所有非叶子节点必须为持久节点，调用上面的api后，只有path参数对应的数据节点是临时节点，其父节点都是持久节点
    }
}