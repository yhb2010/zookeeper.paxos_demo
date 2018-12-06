package com.demo.zookeeper.c5_4curator.create;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用curator来创建一个ZooKeeper客户端
public class Create_Session_Sample {
    public static void main(String[] args) throws Exception{
    	//使用了ExponentialBackoffRetry重试策略，构造方法的参数说明：
    	//baseSleepTimeMs：初始sleep时间
    	//maxRetries：最大重试次数
    	//maxSleepMs：最大sleep时间
    	//随着重试次数的增加，计算出的sleep时间会越来越大，如果该sleep时间在maxSleepMs范围内，那么就使用该sleep，否则使用maxSleepMs，maxRetries定义了最大重试次数，以避免无限制的重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //connectString：zookeeper服务器列表
        //retryPolicy：重试策略，默认四种实现方式：1、ExponentialBackoffRetry，2、RetryNTimes、3、RetryOneTime、4、RetryUntilElapsed
        //sessionTimeoutMs：会话超时时间，单位毫秒，默认60000ms
        //connectionTimeoutMs：连接创建超时时间，单位毫秒，默认15000ms

        //RetryPolicy接口说明：
        //retryCount：已经重试的次数，如果是第一次重试，那么该参数为0
        //elapsedTimeMs：从第一次重试开始已经花费的时间，单位为毫秒
        //sleeper：用于sleep指定时间，建议不要使用Thread.sleep来进行sleep操作
        CuratorFramework client =
        CuratorFrameworkFactory.newClient("127.0.0.145:2181",
        		5000,
        		3000,
        		retryPolicy);
        client.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}