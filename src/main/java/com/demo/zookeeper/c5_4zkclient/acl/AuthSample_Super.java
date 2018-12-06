package com.demo.zookeeper.c5_4zkclient.acl;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

//使用Super权限模式进行权限控制
//acl权限控制给zookeeper管理员带来了一个困惑：如果一个持久节点包含了acl权限控制，而其创建者客户端已经退出或不再使用，那么这些数据
//节点要如何清理呢，这个时候，就需要在acl的super模式下，使用超级管理员权限来进行处理了，要使用超级管理员权限，首先需要在zookeeper
//服务器上开启super模式，方法是在zookeeper服务器启动时，添加如下系统属性：
//-Dzookeeper.DigestAuthenticationProvider.superDigest=foo:kWN6aNSbjcKWPqjiV7cg0N24raU=
public class AuthSample_Super {

    final static String PATH = "/zk-book";
    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper1 = new ZooKeeper("domain1.book.zookeeper:2181",5000,null);
        zookeeper1.addAuthInfo("digest", "foo:true".getBytes());
        zookeeper1.create( PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );

        ZooKeeper zookeeper2 = new ZooKeeper("domain1.book.zookeeper:2181",50000,null);
        //超级管理员可以删除成功
        zookeeper2.addAuthInfo("digest", "foo:zk-book".getBytes());
        System.out.println(zookeeper2.getData( PATH, false, null ));

        ZooKeeper zookeeper3 = new ZooKeeper("domain1.book.zookeeper:2181",50000,null);
        //普通用户删除失败
        zookeeper3.addAuthInfo("digest", "foo:false".getBytes());
        System.out.println(zookeeper3.getData( PATH, false, null ));
    }
}