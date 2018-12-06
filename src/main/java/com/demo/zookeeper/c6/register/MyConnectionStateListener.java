package com.demo.zookeeper.c6.register;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

/**
 * 此类负责监听connection的状态，并在检测到LOST状态时（此时client已经从Zookeeper中掉线）重新注册。
 * 注意，在检测到LOST状态后，上面的代码用了一个while (true) 死循环来不断尝试重新连接Zookeeper server，连不上不罢休。
 * @author dell
 *
 */
public class MyConnectionStateListener implements ConnectionStateListener {

	private String zkRegPathPrefix;

	public MyConnectionStateListener(String zkRegPathPrefix) {
		this.zkRegPathPrefix = zkRegPathPrefix;
	}

	@Override
	public void stateChanged(CuratorFramework curatorFramework,
			ConnectionState connectionState) {
		if (connectionState == ConnectionState.RECONNECTED) {
			try {
				if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
					curatorFramework
							.create()
							.creatingParentsIfNeeded()
							.withMode(CreateMode.EPHEMERAL)
							.forPath(zkRegPathPrefix);
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
			}
		}
	}

}
