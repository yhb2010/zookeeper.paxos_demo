package com.demo.zookeeper.c5_4curator.getdata;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

//使用Curator获取数据内容
public class Get_Data_Sample {

    static String path = "/zk-book8";
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
        Stat stat = new Stat();
        System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
        //读取一个节点的数据内容，返回的是byte[]
        //client.getData().forPath(path)
        //读取一个节点的数据内容，同时获取到该节点的stat，通过传入一个旧的stat变量的方式来存储服务端返回的最新的节点状态信息
        //client.getData().storingStatIn(stat).forPath(path)
    }
}