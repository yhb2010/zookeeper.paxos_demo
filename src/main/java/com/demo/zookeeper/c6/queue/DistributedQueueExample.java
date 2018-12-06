package com.demo.zookeeper.c6.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**DistributedQueue是最普通的一种队列。 它设计以下四个类：
QueueBuilder - 创建队列使用QueueBuilder,它也是其它队列的创建类
QueueConsumer - 队列中的消息消费者接口
QueueSerializer - 队列消息序列化和反序列化接口，提供了对队列中的对象的序列化和反序列化
DistributedQueue - 队列实现类
    QueueConsumer是消费者，它可以接收队列的数据。处理队列中的数据的代码逻辑可以放在QueueConsumer.consumeMessage()中。
    正常情况下先将消息从队列中移除，再交给消费者消费。但这是两个步骤，不是原子的。可以调用Builder的lockPath()消费者加锁，当消费者消费数据
    时持有锁，这样其它消费者不能消费此消息。如果消费失败或者进程死掉，消息可以交给其它进程。这会带来一点性能的损失。最好还是单消费者模式使用
    队列。
 * @author dell
 *
 */
public class DistributedQueueExample {

	private static final String PATH = "/example/queue";

	public static void main(String[] args) throws Exception {
		CuratorFramework clientA = CuratorFrameworkFactory.newClient("127.0.0.145:2181", new ExponentialBackoffRetry(1000, 3));
		clientA.start();
		CuratorFramework clientB = CuratorFrameworkFactory.newClient("127.0.0.145:2181", new ExponentialBackoffRetry(1000, 3));
		clientB.start();

		DistributedQueue<String> queueA = null;
		QueueBuilder<String> builderA = QueueBuilder.builder(clientA, createQueueConsumer("A"), createQueueSerializer(), PATH);
		queueA = builderA.buildQueue();
		queueA.start();

		DistributedQueue<String> queueB = null;
		QueueBuilder<String> builderB = QueueBuilder.builder(clientB, createQueueConsumer("B"), createQueueSerializer(), PATH);
		queueB = builderB.buildQueue();
		queueB.start();

		for (int i = 0; i < 100; i++) {
			queueA.put(" test-A-" + i);
			Thread.sleep(10);
			queueB.put(" test-B-" + i);
		}
		Thread.sleep(1000 * 10);// 等待消息消费完成
		queueB.close();
		queueA.close();
		clientB.close();
		clientA.close();
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
