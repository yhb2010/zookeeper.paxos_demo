package com.demo.zookeeper.c6.masterselect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;

import com.demo.zookeeper.ObjectAndByte;

/**
 * master选举 主工作类
 *
 * @author jerome_s@qq.com
 */
public class WorkServer {

	/** 服务器是否在运行 */
	private volatile boolean running = false;

	private CuratorFramework zkClient;

	/** 主节点路径 */
	private static final String MASTER_PATH = "/master";

	/** 从节点 */
	private RunningData serverData;

	/** 主节点 */
	private RunningData masterData;

	/** 延迟执行 */
	private ScheduledExecutorService delayExector = Executors.newScheduledThreadPool(1);
	//延迟时间5s
	private int delayTime = 5;

	public WorkServer(RunningData runningData){
		this.serverData = runningData;
	}

	public void startServer() throws Exception {
		System.out.println(this.serverData.getName() + "is start!");

		if (running) {
			throw new Exception("server has startup...");
		}

		running = true;

		// 订阅删除事件
		final NodeCache cache = new NodeCache(zkClient, MASTER_PATH, false);
		cache.start(true);
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				//节点删除触发
				if(cache.getCurrentData() == null){
					takeMaster();

					// 对应网络抖动的方法
					// 由于网络抖动，可能误删了master节点导致重新选举，如果master还未宕机，而被其他节点抢到了，
					// 会造成可能有写数据重新生成等资源的浪费。我们这里，增加一个判断，如果上次自己不是master就等待5s在开始争抢master，
					// 这样就能保障没有宕机的master能再次选中为master。
					/*if (masterData != null && masterData.getName().equals(serverData.getName())) {
						takeMaster();
					} else {
						// 延迟5s再争抢
						delayExector.schedule(new Runnable() {
							public void run() {
								takeMaster();
							}
						}, delayTime, TimeUnit.SECONDS);
					}*/
				}
			}
		});

		takeMaster();

	}

	/**
	 * 关闭服务器
	 *
	 * @author jerome_s@qq.com
	 * @throws Exception
	 */
	public void stop() throws Exception {
		if (!running) {
			throw new Exception("server has stoped");
		}
		running = false;

		delayExector.shutdown();

		releaseMaster();
	}

	/**
	 * 争抢master节点
	 *
	 * @author jerome_s@qq.com
	 */
	private void takeMaster() {

		if (!running) {
			return;
		}

		try {
			// 创建临时节点
			ObjectAndByte<RunningData> util = new ObjectAndByte<>();
			zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(MASTER_PATH, util.toByteArray(serverData));
			masterData = serverData;
			System.out.println(serverData.getName() + " is master");

			// 测试: 5s后判断是否是master节点,是的话 释放master节点
			// 释放后,其他节点都是有监听删除事件的,会争抢master
			delayExector.schedule(new Runnable() {
				public void run() {
					if (checkIsMaster()) {
						releaseMaster();
					}
				}
			}, delayTime, TimeUnit.SECONDS);

		} catch (Exception e) {
			ObjectAndByte<RunningData> util = new ObjectAndByte<>();
			RunningData runningData;
			try {
				runningData = util.toObject(zkClient.getData().forPath(MASTER_PATH));
				if (runningData == null) {
					takeMaster();
				} else {
					masterData = runningData;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 释放master
	 *
	 * @author jerome_s@qq.com
	 */
	private void releaseMaster() {
		if (checkIsMaster()) {
			try {
				zkClient.delete().forPath(MASTER_PATH);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查是否是master
	 *
	 * @author jerome_s@qq.com
	 * @return
	 */
	private boolean checkIsMaster() {
		try {
			ObjectAndByte<RunningData> util = new ObjectAndByte<>();
			RunningData eventData = util.toObject(zkClient.getData().forPath(MASTER_PATH));
			masterData = eventData;
			if (masterData.getName().equals(serverData.getName())) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public CuratorFramework getZkClient() {
		return zkClient;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

}
