package com.demo.zookeeper.c5_4curator.queue;

import org.apache.curator.framework.recipes.queue.QueueSerializer;

public class DelayTaskSerializer implements QueueSerializer<String> {

	@Override
    public byte[] serialize(String item) {
        return item.getBytes();
    }

    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes);
    }

}
