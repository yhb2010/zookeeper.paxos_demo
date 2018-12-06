package com.demo.zookeeper.c5_4curator.master;

import java.util.concurrent.TimeUnit;

/**
 * Created by lxb on 2017/2/24.
 * <p>
 * Leader Election
 */
public interface Leader {

    /**判断实例是否是主节点
     * @return
     */
    boolean isLeader();

    /**判断实例是否是主节点，如果不是，则尝试获取
     * @return
     */
    boolean isLeaderGetLeader();

    /**
     * 阻塞直到获得领导权
     */
    public void awaitByLeaderLatch();

    /**尝试获得领导权并超时
     * @param timeout
     * @param unit
     * @return
     */
    public boolean awaitByLeaderLatch(long timeout, TimeUnit unit);

}
