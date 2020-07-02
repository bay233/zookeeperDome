package com.yc.zookeeper.project.zkDome;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * 更新节点数据
 * @author baijiahui
 * @data 2020年2月29日 下午6:05:05
 */
public class Test5 {
	
	private static ZkHelper helper;
	private static ZooKeeper zk;
	
	public static void update(String path, byte[] data) throws KeeperException, InterruptedException{
		zk.setData(path, data, zk.exists(path, true).getVersion());
	}

	public static void main(String[] args) {
		String path = "/yc74Test1";
		byte [] data = "Success".getBytes();
		helper = new ZkHelper();
		try {
			zk = helper.connect();
			update(path, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
