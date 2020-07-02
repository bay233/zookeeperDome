package com.yc.zookeeper.project.zkDome;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.yc.zookeeper.project.zkDome.util.YcZnodeUtil;
/**
 * 为节点添加监控事件
 * @author hp
 *
 */
public class Test4 {
	private static ZkHelper helper;
	private static ZooKeeper zk;
	
	public static Stat znode_exists(String path) throws KeeperException, InterruptedException{
		return zk.exists(path, true);
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		CountDownLatch countDownLatch = new CountDownLatch(5);
		
		String path = "/yc74Test1";
		helper = new ZkHelper();
		zk = helper.connect();
		Stat stat = znode_exists(path);
		
		if (stat == null){
			System.out.println(path + "节点不存在");
			return ;
		}
		MyWatch mw = new MyWatch(zk, path, countDownLatch);
		byte[] data = zk.getData(path, mw, stat);
		
		String dataString = new String (data, "utf-8");
		System.out.println("主程序中获取节点:" + path + "的原始数据为:" + dataString);
		System.out.println(path + "节点信息如下:");
		String info = YcZnodeUtil.printZnodeInfo(stat);
		System.out.println(info);
		
		System.out.println("主程序阻塞......");
		countDownLatch.await();
		helper.close();
		System.out.println("客户端运行完毕，关闭联接");
	}
	
}

class MyWatch implements Watcher{
	private ZooKeeper zk;
	private String path;
	private CountDownLatch countDownLatch;
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
		try {
			 //                            传入本身 重复绑定
			byte[] data = zk.getData(path, MyWatch.this, stat);
			String dataString = new String (data, "utf-8");
			System.out.println("监听程序中获取节点:" + path + "的更新后的数据为:" + dataString);
			System.out.println("节点最新的信息stat为:");
			String info = YcZnodeUtil.printZnodeInfo(stat);
			System.out.println(info);
			countDownLatch.countDown();
			if (countDownLatch.getCount() == 1){
				countDownLatch = new CountDownLatch(Integer.MAX_VALUE);
			}
			System.out.println("当前 countDownLatch " + countDownLatch.getCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
