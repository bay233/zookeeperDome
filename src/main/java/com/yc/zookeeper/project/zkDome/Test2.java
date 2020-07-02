package com.yc.zookeeper.project.zkDome;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建一个节点
 */
public class Test2 {
	private static ZkHelper helper;
	private static ZooKeeper zk;
	
	public static void create(String path, byte[] data) throws KeeperException, InterruptedException{
		String r = zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("创建了节点:" + r);
	}
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		String path = "/yc74Test1";
		byte data[] = "hello".getBytes();
		helper = new ZkHelper();
		zk = helper.connect();
		create(path,data);
		helper.close();
		System.out.println("客户端运行完毕，关闭联接");
	}
}
