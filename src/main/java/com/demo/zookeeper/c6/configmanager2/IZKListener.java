package com.demo.zookeeper.c6.configmanager2;

import org.apache.curator.framework.CuratorFramework;

/**
 * 监听事件接口
 * 所有需要在ZK客户端链接成功后需要做的事件，需要实现这个接口，由上面的ZookeeperFactoryBean统一调度。
 * @author dell
 *
 */
public interface IZKListener {

	void executor(CuratorFramework client);

}
