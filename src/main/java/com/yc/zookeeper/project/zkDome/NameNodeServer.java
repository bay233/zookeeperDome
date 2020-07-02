package com.yc.zookeeper.project.zkDome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class NameNodeServer {
	private static ZkHelper helper;
	private static ZooKeeper zk;
	private String parentNode = "/servers";
	
	public void getServerLIst() throws KeeperException, InterruptedException{
		List<String> children = zk.getChildren(parentNode, true);
		
		List<String> servers = new ArrayList<String>();
		
		for (String child : children) {
			byte [] data = zk.getData(parentNode + "/", false, null);
			servers.add(new String(data));
		}
		System.out.println("目前服务器有:" + servers);
	}
	
	public void business() throws InterruptedException{
		System.out.println("client is working....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		NameNodeServer server = new NameNodeServer();
		helper = new ZkHelper();
		zk = helper.connect();
		server.getServerLIst();
		server.business();
	}

}
