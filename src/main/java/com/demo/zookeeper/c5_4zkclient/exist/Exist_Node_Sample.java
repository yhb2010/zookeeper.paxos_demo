package com.demo.zookeeper.c5_4zkclient.exist;

import org.I0Itec.zkclient.ZkClient;

//ZkClient检测节点是否存在
public class Exist_Node_Sample {
    public static void main(String[] args) throws Exception {
    	String path = "/zk-book";
    	ZkClient zkClient = new ZkClient("127.0.0.145:2181", 2000);
        System.out.println("Node " + path + " exists " + zkClient.exists(path));
    }
}