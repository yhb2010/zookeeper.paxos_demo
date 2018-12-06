package com.demo.zookeeper.c5_4curator.barrier;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Barrier是一种用来控制多线程之间同步的经典方式，jdk自带了CyclicBarrier。
//可以看到，多线程在并发的情况下，都会准备的等待所有线程都处理就绪状态后才开始同时执行其它业务逻辑。
//这是在同一个jvm下没问题，但在分布式环境下又该如何解决呢？
public class Recipes_CyclicBarrier {

	public static CyclicBarrier barrier = new CyclicBarrier( 3 );
	public static void main( String[] args ) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool( 3 );
		executor.submit( new Thread( new Runner( "1号选手" ) ) );
		executor.submit( new Thread( new Runner( "2号选手" ) ) );
		executor.submit( new Thread( new Runner( "3号选手" ) ) );
		executor.shutdown();
	}
}

class Runner implements Runnable {
	private String name;
	public Runner( String name ) {
		this.name = name;
	}
	public void run() {
		System.out.println( name + " 准备好了." );
		try {
			Recipes_CyclicBarrier.barrier.await();
		} catch ( Exception e ) {}
		System.out.println( name + " 起跑!" );
	}
}