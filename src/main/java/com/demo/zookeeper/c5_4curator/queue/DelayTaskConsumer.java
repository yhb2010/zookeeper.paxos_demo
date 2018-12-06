package com.demo.zookeeper.c5_4curator.queue;

import java.text.MessageFormat;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.state.ConnectionState;

//任务消费者
//curator的消费者需要实现QueueConsumer接口，在这里我们做的逻辑就是拿到任务描述（这里简单起见，任务描述就是资讯id），然后发布相应的资讯。
public class DelayTaskConsumer implements QueueConsumer<String> {

	@Override
	public void consumeMessage(String message) throws Exception {
		System.out.println(MessageFormat.format("发布资讯。id - {0} , timeStamp - {1} , " + "threadName - {2}", message, System.currentTimeMillis(), Thread.currentThread() .getName()));
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		System.out.println(MessageFormat.format( "State change . New State is - {0}", newState));
	}

}