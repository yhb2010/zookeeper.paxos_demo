package com.demo.zookeeper.c6.masterselect2;

import java.io.Serializable;

/**
 * master选举 描述Worker Server的基本信息
 *
 * @author jerome_s@qq.com
 */
public class RunningData implements Serializable {

	private static final long serialVersionUID = 4260577459043203630L;
	// 服务器id
	private Long cid;
	// 服务器名称
	private String name;

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
