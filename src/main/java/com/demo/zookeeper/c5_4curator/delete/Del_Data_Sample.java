package com.demo.zookeeper.c5_4curator.delete;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

//使用Curator删除节点
public class Del_Data_Sample {

    static String path = "/zk-book8/c1";
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
        client.getData().storingStatIn(stat).forPath(path);
        client.delete().deletingChildrenIfNeeded()
                       .withVersion(stat.getVersion()).forPath("/zk-book8");
        //删除一个节点，只能删除叶子节点
        //client.delete().forPath(path)
        //删除一个节点，并递归删除其所有子节点
        //client.delete().deletingChildrenIfNeeded().forPath(path)
        //删除一个节点，强制指定版本进行删除
        //client.delete().withVersion(version).forPath(path)
        //删除一个节点，强制保证删除
        //client.delete().guaranteed().forPath(path)
        //guaranteed()接口是一个强制保障措施，只要客户端会话有效，那么curator会在后台持续进行删除操作，直到节点删除成功
    }
}