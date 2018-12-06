package com.demo.zookeeper.c6.naming;

import com.demo.zookeeper.c6.naming.IdMaker.RemoveMethod;

public class TestIdMaker extends Thread {

	public static IdMaker idMaker = new IdMaker("127.0.0.145:2181", "/NameService/IdGen", "ID");

	public void run() {
        for (int i = 0; i < 10; i++) {
            try {
            	String id = idMaker.generateId(RemoveMethod.DELAY);
				System.out.println(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	public static void main(String[] args) throws Exception {
		idMaker.start();

		Thread mTh1=new TestIdMaker();
		Thread mTh2=new TestIdMaker();
		mTh1.start();
		mTh2.start();
	}

}
