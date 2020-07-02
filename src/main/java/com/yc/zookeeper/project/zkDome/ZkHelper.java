package com.yc.zookeeper.project.zkDome;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper.States;

public class ZkHelper {
	private String connectString = "bay1:2181,bay2:2181,bay3:2181";
	private int sessionTimeout = 2000;
	private ZooKeeper zkClient = null;
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private Logger logger = Logger.getLogger(ZkHelper.class);
	
	
	
	public ZkHelper(String connectString, int sessionTimeout) {
		super();
		this.connectString = connectString;
		this.sessionTimeout = sessionTimeout;
	}

	public ZkHelper() {
		super();
	}

	public ZooKeeper connect() throws IOException, InterruptedException{
		logger.info("zk客户端初始化中....");
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				if(event.getState() == KeeperState.SyncConnected){
					logger.info("zk客户端与服务器连接成功！");
					countDownLatch.countDown();  //阻塞值减一
				}
			}
		});
		countDownLatch.await();  // 归零之后停止阻塞
		return zkClient;
	}
	
	public void close(){
		logger.info("关闭zookeeper连接....");
		if (zkClient != null && zkClient.getState() == States.CONNECTED ){
			try {
				zkClient.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("成功关闭连接！");
	}
	
/*	public static void main(String[] args) throws IOException, InterruptedException {
		ZkHelper zkHelper = new ZkHelper();
		ZooKeeper connect = zkHelper.connect();
		System.out.println(connect);
		zkHelper.close();
	}*/
}
