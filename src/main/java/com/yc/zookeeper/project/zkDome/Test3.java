package com.yc.zookeeper.project.zkDome;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.yc.zookeeper.project.zkDome.util.YcZnodeUtil;

/**
 *	获取节点信息 
 */
public class Test3 {
	private static ZkHelper helper;
	private static ZooKeeper zk;
	
	public static Stat znode_exists(String path) throws KeeperException, InterruptedException{
		return zk.exists(path, true);
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		String path = "/yc74Test1";
		byte data[] = "hello".getBytes();
		helper = new ZkHelper();
		zk = helper.connect();
		Stat stat = znode_exists(path);
		helper.close();
		
		if (stat == null){
			System.out.println(path + "节点不存在");
		}else{
			System.out.println(path + "节点信息如下:");
			String info = YcZnodeUtil.printZnodeInfo(stat);
			System.out.println(info);
		}
		System.out.println("客户端运行完毕，关闭联接");
	}
}
