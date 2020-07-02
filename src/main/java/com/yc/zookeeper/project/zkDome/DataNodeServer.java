package com.yc.zookeeper.project.zkDome;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class DataNodeServer {

	private static ZkHelper helper;
	private static ZooKeeper zk;
	private String parentNode = "/servers";
	
	public void registServer(String hostname) throws KeeperException, InterruptedException{
		String create = zk.create(parentNode + "/server", hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname + "is online" + create);
	}
	
	public void business() throws InterruptedException{
		System.out.println("client is working....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws KeeperException, InterruptedException {
		String hostname = "dataNode2";
		if (args != null && args.length > 0){
			hostname = args[0];
		}
		
		DataNodeServer server = new DataNodeServer();
		
		server.registServer(hostname);
		
		server.business();
	}

}
