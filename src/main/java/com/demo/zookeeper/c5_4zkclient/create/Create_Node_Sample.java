package com.demo.zookeeper.c5_4zkclient.create;
import org.I0Itec.zkclient.ZkClient;

// 使用ZkClient创建节点
public class Create_Node_Sample {

    public static void main(String[] args) throws Exception {
    	ZkClient zkClient = new ZkClient("127.0.0.145:2181", 5000);
        String path = "/zk-book/z2";
        //path：指定数据节点的节点路径，即api调用的目的是创建该节点
        //data：节点的初始数据内容，可以传入null
        //mode：节点类型，是一个枚举类型，通常有4中可选的节点类型
        //acl：节点的acl策略
        //callback：注册一个异步回调函数
        //context：用于传递一个对象，可以在执行回调函数的时候使用
        //createParents：指定是否需要创建父节点，原生的zookeeper api无法递归创建节点，也就是说只有在父节点存在的情况下才能创建，
        //通过该参数，ZkClient可以递归创建父节点。
        zkClient.createPersistent(path, true);
    }
}