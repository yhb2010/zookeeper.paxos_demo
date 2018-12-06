package com.demo.zookeeper.c5_4curator.queue;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//zookeeper的znode同样可以用来存储数据，那么我们就可以利用这一点来实现延时任务。实际上，著名的zookeeper客户端curator就提供了基于zookeeper的延时任务API
public class DelayTaskTest {

	public static void main(String[] args) throws Exception{
        DelayTaskProducer producer=new DelayTaskProducer();
        long now=new Date().getTime();
        System.out.println(MessageFormat.format("start time - {0}",now));
        producer.produce("1",now+TimeUnit.SECONDS.toMillis(5));
        producer.produce("2",now+TimeUnit.SECONDS.toMillis(10));
        producer.produce("3",now+TimeUnit.SECONDS.toMillis(15));
        producer.produce("4",now+TimeUnit.SECONDS.toMillis(20));
        producer.produce("5",now+TimeUnit.SECONDS.toMillis(2000));
        TimeUnit.HOURS.sleep(1);
    }

}
