package com.demo.zookeeper.c5_4curator.lock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用Curator实现分布式共享锁功能
/**读写锁：
简单介绍一下读写锁，在使用读写锁时， 多个客户端（线程）可以同时获取 “读锁”， 但是“写入锁”是排它的，只能单独获取。
1、假设A,B线程获取到 “读锁”， 这时C线程就不能获取 “写锁”。
2、假设C线程获取了“写锁”，那么A,B线程就不能获取“读锁”。
 *
 */
public class Recipes_ShareLock {

	static String lock_path = "/curator_recipes_lock_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

	public static void main(String[] args) throws Exception {
		client.start();
		InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(client, lock_path);
		final InterProcessMutex readLock = readWriteLock.readLock();
		final InterProcessMutex writeLock = readWriteLock.writeLock();

		List<Thread> jobs = new ArrayList<Thread>();

		for (int i = 0; i < 3; i++) {
			Thread t = new Thread("锁  " + i) {
				public void run() {
					readWriterLock(readLock, writeLock);
				}
			};
			jobs.add(t);
		}

		for (Thread thread : jobs) {
			thread.start();
		}
	}

	/**
     *
     * @描述：读写锁演示
     * @return void
     * @exception
     * @createTime：2016年5月19日
     * @author: songqinghu
     */
	private static void readWriterLock(InterProcessLock readLock, InterProcessLock writeLock) {
		try {
            readLock.acquire();
            System.out.println(Thread.currentThread() + "获取到读锁");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //在读锁没释放之前不能读取写锁。
                        writeLock.acquire();
                        System.out.println(Thread.currentThread() + "获取到写锁");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            writeLock.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            //停顿3000毫秒不释放锁，这时其它线程可以获取读锁，却不能获取写锁。
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
				readLock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

	}

}