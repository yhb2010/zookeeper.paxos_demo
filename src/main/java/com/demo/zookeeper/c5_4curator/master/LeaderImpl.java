package com.demo.zookeeper.c5_4curator.master;

import java.io.EOFException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lxb on 2017/2/28.
 * <p>
 * * Leader Election
 */
public class LeaderImpl implements Leader {

    private static Logger logger = LoggerFactory.getLogger(LeaderImpl.class);
    private LeaderLatch leaderLatch;

    public LeaderImpl(CuratorFramework zk, String nodeName) {
    	try {
            leaderLatch = new LeaderLatch(zk, nodeName);
            LeaderLatchListener leaderLatchListener = new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    logger.info("[LeaderLatch]我是主节点, id={}", leaderLatch.getId());
                }

                @Override
                public void notLeader() {
                    logger.info("[LeaderLatch]我不是主节点, id={}", leaderLatch.getId());
                }
            };
            leaderLatch.addListener(leaderLatchListener);
            leaderLatch.start();
        } catch (Exception e) {
            logger.error("c创建LeaderLatch失败, path={}, error={}", nodeName, e.getMessage());
        }
    }

    @Override
    public boolean isLeader() {
        return leaderLatch == null || leaderLatch.hasLeadership();
    }

     public void awaitByLeaderLatch() {
         try {
             leaderLatch.await();
         } catch (InterruptedException | EOFException e) {
             e.printStackTrace();
         }
     }

     public boolean awaitByLeaderLatch(long timeout, TimeUnit unit) {
         boolean hasLeadership = false;
         try {
             hasLeadership = leaderLatch.await(timeout, unit);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         return  hasLeadership;
     }

	@Override
	public boolean isLeaderGetLeader() {
		if(isLeader()){
			return true;
		}
		return awaitByLeaderLatch(3, TimeUnit.SECONDS);
	}

}
