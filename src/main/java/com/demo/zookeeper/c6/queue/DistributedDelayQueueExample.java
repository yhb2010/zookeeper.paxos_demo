package com.demo.zookeeper.c6.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedDelayQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**DistributedDelayQueue介绍
放入元素时可以指定delayUntilEpoch：queue.put(aMessage, delayUntilEpoch);
注意：delayUntilEpoch不是离现在的一个时间间隔，比如20毫秒，而是未来的一个时间戳，如 System.currentTimeMillis() + 10秒。
如果delayUntilEpoch的时间已经过去，消息会立刻被消费者接收。
 * @author dell
 *
 */
public class DistributedDelayQueueExample {

	private static final String PATH = "/example/queue";
    public static void main(String[] args) throws Exception
    {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.145:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        DistributedDelayQueue<String> queue = null;
        QueueConsumer<String> consumer = createQueueConsumer("A");
        QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
        queue = builder.buildDelayQueue();
        queue.start();
        for (int i = 0; i < 10; i++)
        {
            queue.put("test-" + i, System.currentTimeMillis() + 3000);
        }
        System.out.println("put 完成！");
        Thread.sleep(1000 * 5);
        queue.close();
        client.close();
        System.out.println("OK!");
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
