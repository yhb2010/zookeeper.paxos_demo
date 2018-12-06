package com.demo.zookeeper.c5_3.acl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

//使用含权限信息的ZooKeeper会话创建数据节点
public class AuthSample {

    final static String PATH = "/zk-book-auth_test";
    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("127.0.0.145:2181", 50000, null);
        //scheme：权限控制模式，分为world、auth、digest、ip和super
        //auth：具体的权限信息，下例中是foo:true，类似username:password
        zookeeper.addAuthInfo("digest", "foo:true".getBytes());
        //用包含权限信息的客户端创建节点
        zookeeper.create( PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );
        Thread.sleep( Integer.MAX_VALUE );
    }
}