package com.demo.zookeeper.c5_4zkclient.create;

import java.io.IOException;
import org.I0Itec.zkclient.ZkClient;

// 使用ZkClient来创建一个ZooKeeper客户端
public class Create_Session_Sample {
    /**zkClient通过内部包装，将会话创建过程同步化了，这对于开发人员来说非常方便。
     * IZkConnection接口默认提供了两个实现，分别是ZkConnection和InMemoryConnection，前者是最常用的实现方式，
     * 通常开发者不需要对IZkConnection进行改造，直接使用ZkConnection实现就可以完成大部分业务需求。
     * zkSerializer接口允许用户传入一个序列化实现，如hessian、kryo，默认情况下，使用java自带的序列化方式进行对象的序列化。
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
    	//zkServers：zookeeper服务器列表
    	//sessionTimeout：会话超时时间，单位为毫秒，默认为30000ms
    	//connectionTimeout：连接创建超时时间，单位为毫秒，此参数表明如果在这个时间段内还是无法和zookeeper建立连接，那么就放弃连接，直接抛异常
    	//connection：IZkConnection接口的实现类
    	//zkSerializer：自定义序列化器
    	ZkClient zkClient = new ZkClient("127.0.0.145:2181", 5000);
    	System.out.println("ZooKeeper session established.");
    }
}