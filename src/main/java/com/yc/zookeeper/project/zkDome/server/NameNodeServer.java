package com.yc.zookeeper.project.zkDome.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.yc.zookeeper.project.zkDome.ZkHelper;
import com.yc.zookeeper.project.zkDome.util.YcZnodeUtil;

public class NameNodeServer {

	private static String parentNode = "/servers";
	private static ZkHelper zkHelper;
	private static ZooKeeper zk;
	private static Logger logger = Logger.getLogger(DataNodeServer.class);
	
	
	private void initMainNode() throws IOException, InterruptedException, KeeperException{
		zkHelper = new ZkHelper();
		zk = zkHelper.connect();
		
		try {
			zk.exists(parentNode, true);
		} catch (KeeperException e) {
			zk.create(parentNode, "this is yc datanade cluster".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
	
	private void getServerList() throws InterruptedException, KeeperException{
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		MyWatch mw = new MyWatch(zk, parentNode, countDownLatch);
		
		List<String> list = zk.getChildren(parentNode, mw);
		logger.info("主程序启动,得到当前dataNode列表:");
		for (String s : list) {
			byte[] data = zk.getData(parentNode+"/"+s, false, null);
			logger.info(s);
		}
		countDownLatch.await();
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		NameNodeServer server = new NameNodeServer();
		server.initMainNode();
		server.getServerList();
		zkHelper.close();
		
	}

}



class MyWatch implements Watcher{
	private ZooKeeper zk;
	private String path;
	private CountDownLatch countDownLatch;
	private static Logger logger = Logger.getLogger(DataNodeServer.class);
	public MyWatch() {
		super();
	}

	public MyWatch(ZooKeeper zk, String path,CountDownLatch countDownLatch) {
		super();
		this.zk = zk;
		this.path = path;
		this.countDownLatch = countDownLatch;
	}


	@Override
	public void process(WatchedEvent event) {
		Stat stat = new Stat();
		// 获取监听事件类型
		if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged){
			logger.info("子节点变化！");
			try {
				List<String> list = zk.getChildren(path,  MyWatch.this);
				showChildrenInfo(list);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
			
		}else if (event.getType() == Watcher.Event.EventType.NodeDataChanged){
			try {
				 //                            传入本身 重复绑定
				byte[] data = zk.getData(path, MyWatch.this, stat);
				String dataString = new String (data, "utf-8");
				logger.info("监听程序中获取节点:" + path + "的更新后的数据为:" + dataString);
				logger.info("节点最新的信息stat为:");
				String info = YcZnodeUtil.printZnodeInfo(stat);
				logger.info(info);
				
				logger.info("当前 countDownLatch " + countDownLatch.getCount());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*countDownLatch.countDown();
		if (countDownLatch.getCount() == 1){
			countDownLatch = new CountDownLatch(Integer.MAX_VALUE);
		}*/
	}

	private void showChildrenInfo(List<String> list) {
		logger.info("当前在线的dataNode有:" + list.size());
		logger.info(list);
		for (String s: list) {
			logger.info("*****"+s+"*****");
			try {
				byte[] data = zk.getData(path+"/"+s, false, null);
				Properties p = (Properties)deserilizable(data);
				logger.info(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Object deserilizable(byte[] data){
		Object obj = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}
	
}
