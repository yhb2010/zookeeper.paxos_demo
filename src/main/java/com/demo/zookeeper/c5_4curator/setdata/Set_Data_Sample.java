package com.demo.zookeeper.c5_4curator.setdata;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

//使用Curator更新数据内容
public class Set_Data_Sample {

    static String path = "/zk-book7";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        //client.delete().deletingChildrenIfNeeded().forPath( path );
        client.create()
              .creatingParentsIfNeeded()
              .withMode(CreateMode.EPHEMERAL)
              .forPath(path, "init".getBytes());
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        //第一次使用最新的stat变量进行更新，更新成功；第二次使用过期的stat变量进行更新，抛出异常KeeperErrorCode=BadVersion
        System.out.println("Success set node for : " + path + ", new version: "
                + client.setData().withVersion(stat.getVersion()).forPath(path).getVersion());
        try {
            client.setData().withVersion(stat.getVersion()).forPath(path);
        } catch (Exception e) {
            System.out.println("Fail set node due to " + e.getMessage());
        }
        //更新一个节点的数据内容，返回一个stat对象
        //client.setData().forPath(path)
        //更新一个节点的数据内容，强制指定版本进行更新
        //client.setData().withVersion(version).forPath(path)
        //withVersion接口就是用来实现CAS的，version通常是从一个旧的stat对象中获取的
    }
}