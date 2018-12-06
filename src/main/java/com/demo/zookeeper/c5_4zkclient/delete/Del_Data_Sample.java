package com.demo.zookeeper.c5_4zkclient.delete;

import org.I0Itec.zkclient.ZkClient;

//ZkClient删除节点数据
public class Del_Data_Sample {
	public static void main(String[] args) throws Exception {
		String path = "/zk-book2";
    	ZkClient zkClient = new ZkClient("127.0.0.145:2181", 2000);
        zkClient.createPersistent(path, "");
        zkClient.createPersistent(path+"/c2", "");
        //path：数据节点的完整节点路径
        //callback：注册一个异步回调函数
        //context：用于传递上下文信息的对象
        //zkClient.delete(path);
        //完成逐层遍历删除节点
        zkClient.deleteRecursive(path);
    }
}