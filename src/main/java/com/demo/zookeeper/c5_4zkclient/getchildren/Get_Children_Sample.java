package com.demo.zookeeper.c5_4zkclient.getchildren;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

// ZkClient获取子节点列表。
public class Get_Children_Sample {

    public static void main(String[] args) throws Exception {

    	String path = "/zk-book2";
        ZkClient zkClient = new ZkClient("127.0.0.145:2181", 5000);
        //没有Watch功能，通过subscribeChildChanges注册监听。监听需要实现IZkChildListener接口。
        //parentPath：字节点变更通知对应的父节点的节点路径
        //currentChileds：子节点的相对路径列表，如果没有子节点，那么会传入null
        //IZkChildListener的事件说明：
        //新增子节点：指定节点nodeA新增子节点。此时parentPath收到的是nodeA的全路径，currentChileds最新的子节点列表
        //减少子节点：指定节点nodeA减少子节点。此时parentPath收到的是nodeA的全路径，currentChileds最新的子节点列表，可能是null
        //删除节点nodeA：指定节点nodeA被删除。此时parentPath收到的是nodeA的全路径，currentChileds是null
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
            }
        });

        zkClient.createPersistent(path);
        Thread.sleep( 1000 );
        System.out.println(zkClient.getChildren(path));
        Thread.sleep( 1000 );
        zkClient.createPersistent(path + "/c1");
        Thread.sleep( 1000 );
        zkClient.delete(path + "/c1");
        Thread.sleep( 1000 );
        zkClient.delete(path);
        //针对上面的例子可以得出结论：
        //1、客户端可以对一个不存在的节点进行子节点更变的监听
        //2、一旦客户端对一个节点注册了子节点列表变更监听之后，那么当该节点的子节点列表发生变更的时候，服务端都会通知客户端，并将最新的子节点列表发送给客户端
        //3、该节点本身的创建或删除，也会通知客户端
        //和原生zookeeper提供的watch不同，zkClient的listener不是一次性的，客户端只需要注册一次就会一直生效
    }
}