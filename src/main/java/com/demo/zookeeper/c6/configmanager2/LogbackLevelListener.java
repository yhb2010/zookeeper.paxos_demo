package com.demo.zookeeper.c6.configmanager2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Logback监听实现
 * 这里主要用到Curator的NodeCache类，它的主要功能是用来监听znode本身的变化，并可以获取当前值，而且会自动重复监听，简化了原生API开发的繁琐过程。
 * @author dell
 *
 */
public class LogbackLevelListener implements IZKListener {

	//获取logback实例
    Logger log = (Logger) LoggerFactory.getLogger(this.getClass());

    private String path;

    //Logback日志级别ZNode
    public LogbackLevelListener(String path) {
        this.path = path;
    }

    @Override
    public void executor(CuratorFramework client) {

        //使用Curator的NodeCache来做ZNode的监听，不用我们自己实现重复监听
        final NodeCache cache = new NodeCache(client, path);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {

                byte[] data = cache.getCurrentData().getData();

                //设置日志级别
                if (data != null) {
                    String level = new String(data);
                    Logger logger = (Logger) LoggerFactory.getLogger("root");
                    Level newLevel = Level.fromLocationAwareLoggerInteger(Integer.parseInt(level));
                    logger.setLevel(newLevel);
                    System.out.println("Setting logback new level to :" + newLevel.levelStr);
                }
            }
        });
        try {
            cache.start(true);
        } catch (Exception e) {
            log.error("Start NodeCache error for path: {}, error info: {}", path, e.getMessage());
        }
    }

}
