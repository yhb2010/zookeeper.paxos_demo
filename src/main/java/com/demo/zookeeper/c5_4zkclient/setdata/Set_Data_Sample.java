package com.demo.zookeeper.c5_4zkclient.setdata;

import org.I0Itec.zkclient.ZkClient;

//ZkClient更新节点数据
public class Set_Data_Sample {

    public static void main(String[] args) throws Exception {
    	String path = "/zk-book2";
    	ZkClient zkClient = new ZkClient("127.0.0.145:2181", 2000);
        zkClient.createEphemeral(path, new Integer(1));
        //path：数据节点的完整节点路径
        //data：数据内容，可以是null
        //expectedVersion：预期的数据版本，zookeeper的数据节点有数据版本的概念，可以使用这个数据版本来实现类似cas的原子操作
        zkClient.writeData(path, new Integer(1));
    }
}