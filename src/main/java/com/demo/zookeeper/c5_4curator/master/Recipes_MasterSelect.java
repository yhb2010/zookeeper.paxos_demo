package com.demo.zookeeper.c5_4curator.master;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

//master选举：选择一个根节点，例如/master_select，多台机器同时向该节点创建一个子节点/master_select/lock，利用zookeeper的特性，
//最终只有一台机器能够创建成功，成功的那台机器就作为master。
public class Recipes_MasterSelect {

	static String master_path = "/curator_recipes_master_path";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.145:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public static void main( String[] args ) throws Exception {
    	client.start();
        LeaderSelector selector = new LeaderSelector(client,
        		master_path,
        		//会在获取master权利后，回调该监听。需要注意的是，一旦执行完takeLeadership方法，curator就会立即释放
        		//master权利，然后重新开始新一轮的master选举。
        		new LeaderSelectorListenerAdapter() {
		            public void takeLeadership(CuratorFramework client) throws Exception {
		                System.out.println("成为Master角色");
		                Thread.sleep( 3000 );
		                System.out.println( "完成Master操作，释放Master权利" );
		            }
		        });
        selector.autoRequeue();
        selector.start();
        Thread.sleep( Integer.MAX_VALUE );
	}
}