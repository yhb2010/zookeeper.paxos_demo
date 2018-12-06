package com.demo.zookeeper.c5_4zkclient.getchildren;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

//ZkClient获取节点数据
public class Get_Data_Sample {
    public static void main(String[] args) throws Exception {

    	String path = "/zk-book4";
        ZkClient zkClient = new ZkClient("127.0.0.145:2181", 5000);
        zkClient.createEphemeral(path, "123");

        zkClient.subscribeDataChanges(path, new IZkDataListener() {
        	//监听节点删除事件
        	//删除节点nodeA：指定节点nodeA被删除，dataPath收到的是nodeA的全路径
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Node " + dataPath + " deleted.");
            }
            //监听节点内容变更事件：
            //节点数据变化：指定节点nodeA的数据内容或是数据版本发生变更，都会触发这个事件，此时dataPath收到的是nodeA的全路径，data是最新的数据节点内容
            //dataPath：事件通知对应的节点路径
            //data：最新的数据内容
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Node " + dataPath + " changed, new data: " + data);
            }
        });

        //returnNullIfPathNotExists：默认情况下调用该api，如果指定节点不存在，那么会抛异常，如果设置了该参数，那么如果指定节点不存在，就直接返回null
        //stat：指定数据节点的节点状态信息，用法是在接口中传入一个旧的stat变量，该stat变量会在方法执行过程中，被来自服务端响应的新stat对象替换。
        System.out.println(zkClient.readData(path));
        zkClient.writeData(path,"456");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep( Integer.MAX_VALUE );
    }
}