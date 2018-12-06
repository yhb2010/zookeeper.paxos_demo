package com.demo.zookeeper.c6.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedPriorityQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**DistributedPriorityQueue介绍
    通过builder.buildPriorityQueue(minItemsBeforeRefresh)方法创建。
    当优先级队列得到元素增删消息时，它会暂停处理当前的元素队列，然后刷新队列。minItemsBeforeRefresh指定刷新前当前活动的队列的最小数量。主要设置你的程序可以容忍的不排序的最小值。
    放入队列时需要指定优先级：queue.put(aMessage, priority);
 * @author dell
 *
 */
public class DistributedPriorityQueueExample {

	private static final String PATH = "/example/queue";
    public static void main(String[] args) throws Exception
    {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.145:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        DistributedPriorityQueue<String> queue = null;
        QueueConsumer<String> consumer = createQueueConsumer("A");
        QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
        queue = builder.buildPriorityQueue(0);
        queue.start();
        for (int i = 0; i < 5; i++)
        {
            int priority = (int) (Math.random() * 100);
            System.out.println("test-" + i + " 优先级:" + priority);
            queue.put("test-" + i, priority);
            Thread.sleep(10);
        }
        Thread.sleep(1000 * 2);
        queue.close();
        client.close();
    }

    /** 队列消息序列化实现类 */
	private static QueueSerializer<String> createQueueSerializer() {
		return new QueueSerializer<String>() {
			@Override
			public byte[] serialize(String item) {
				return item.getBytes();
			}

			@Override
			public String deserialize(byte[] bytes) {
				return new String(bytes);
			}
		};
	}

	/** 定义队列消费者 */
	private static QueueConsumer<String> createQueueConsumer(final String name) {
		return new QueueConsumer<String>() {
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				System.out.println("连接状态改变: " + newState.name());
			}

			@Override
			public void consumeMessage(String message) throws Exception {
				System.out.println("消费消息(" + name + "): " + message);
			}
		};
	}

}
