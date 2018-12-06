package com.demo.zookeeper.c6.configmanager;

public interface Config {

	byte[] getConfig(String path) throws Exception;

}
