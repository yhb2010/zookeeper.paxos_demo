package com.demo.zookeeper.c5_4curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**InterProcessSemaphoreV2信号量与多线程中的Semaphonre信息量含义是一致的，
即同时最多只能允许指定数量的线程访问临界资源。
通过InterProcessSemaphoreV2创建信息量，然后使用acquire()获取访问权限，
使用returnLease(lease)翻译访问权限。
 * @author Administrator
 *
 */
public class Semaphore {
	
	public static void main(String[] args) throws Exception {
        //创建zookeeper客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient("ip104:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        //指定锁路径
        String lockPath = "/zkLockRoot/lock_1";
        //创建信号量，指定同时最大访问数为3个
        InterProcessSemaphoreV2 semaphoreLock = new InterProcessSemaphoreV2(client, lockPath, 3);

        //生成线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        Consumer<InterProcessSemaphoreV2> consumer = (InterProcessSemaphoreV2 semaphore)->{
            try{
                List<Callable<String>> callList = new ArrayList<>();
                Callable<String> call = () -> {
                    Lease lease = semaphore.acquire();
                    try{
                        System.out.println(Thread.currentThread() + "  acquire semaphore");
                        TimeUnit.MILLISECONDS.sleep(2000);
                    }catch (Exception e){
                    }finally {
                        semaphore.returnLease(lease);
                        System.out.println(Thread.currentThread() + "  release semaphore");
                    }
                    return "true";
                };
                //5个并发线程
                for (int i = 0; i < 5; i++) {
                    callList.add(call);
                }
                List<Future<String>> futures = executor.invokeAll(callList);
            }catch (Exception e){
            }
        };

        //读锁测试(多个线程可同时获取读锁)
        System.out.println("5个并发线程,信号量测试");
        consumer.accept(semaphoreLock);

        executor.shutdown();
    }

}
