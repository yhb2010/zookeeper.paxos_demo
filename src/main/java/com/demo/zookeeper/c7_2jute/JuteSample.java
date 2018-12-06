package com.demo.zookeeper.c7_2jute;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.ByteBufferInputStream;

//使用Jute进行序列化
public class JuteSample {

	//jute对对象进行序列化和反序列化的过程：
	//1、实体类需要实现Record接口的serialize和deserialize方法
	//2、构建一个序列化器BinaryOutputArchive
	//3、序列化：调用实体类的serialize方法，将对象序列化到指定tag中去，在本例中就将MockReqHeader对象序列化到header中去
	//4、反序列化：调用实体类的deserialize，从指定的tag中反序列化出数据内容
	public static void main( String[] args ) throws Exception {
		//开始序列化
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
		new MockReqHeader( 0x34221eccb92a34el, "ping" ).serialize(boa, "header");
		//这里通常是TCP网络传输对象
		ByteBuffer bb = ByteBuffer.wrap( baos.toByteArray() );
		//开始反序列化
		ByteBufferInputStream bbis = new ByteBufferInputStream(bb);
		BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
		MockReqHeader header2 = new MockReqHeader();
		header2.deserialize(bbia, "header");
		bbis.close();
		baos.close();
	}
}
