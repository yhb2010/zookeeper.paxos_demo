package com.demo.zookeeper.c6.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedIdQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**DistributedIdQueue的使用与DistributedQueue队列的区别是：
通过下面方法创建：builder.buildIdQueue()
放入元素时：queue.put(aMessage, messageId);
移除元素时：int numberRemoved = queue.remove(messageId);
2.编写示例程序
在这个例子中，有些元素还没有被消费者消费时就移除了，这样消费者不会收到删除的消息。(此示例是根据DistributedQueue例子修改而来)
 * @author dell
 *
 */
public class DistributedIdQueueExample {

	private static final String PATH = "/example/queue";
    public static void main(String[] args) throws Exception
    {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.145:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        DistributedIdQueue<String> queue = null;
        QueueConsumer<String> consumer = createQueueConsumer("A");
        QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
        queue = builder.buildIdQueue();
        queue.start();
        for (int i = 0; i < 10; i++)
        {
            queue.put(" test-" + i, "Id" + i);
            Thread.sleep((long) (50 * Math.random()));
            queue.remove("Id" + i);
        }
        Thread.sleep(1000 * 3);
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
