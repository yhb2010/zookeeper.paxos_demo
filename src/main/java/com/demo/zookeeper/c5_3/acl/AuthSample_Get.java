package com.demo.zookeeper.c5_3.acl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

//使用无权限信息的ZooKeeper会话访问含权限信息的数据节点
public class AuthSample_Get {

    final static String PATH = "/zk-book-auth_test";
    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper1 = new ZooKeeper("127.0.0.145:2181", 5000, null);
        zookeeper1.addAuthInfo("digest", "foo:true".getBytes());
        //用包含权限信息的客户端创建节点
        zookeeper1.create( PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );

        //用不包含权限信息的客户端访问节点
        //Exception in thread "main" org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk-book-auth_test
        ZooKeeper zookeeper2 = new ZooKeeper("127.0.0.145:2181", 50000, null);
        zookeeper2.getData( PATH, false, null );
    }
}