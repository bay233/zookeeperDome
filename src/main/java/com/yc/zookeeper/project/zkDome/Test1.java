package com.yc.zookeeper.project.zkDome;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
/**
 * 获取节点下的所有节点
 * 
 * @author baijiahui
 * @data 2020年3月23日 下午8:10:00
 */
public class Test1 {
	private static String connectString = "bay1:2181,bay2:2181,bay3:2181";
	private static int sessionTimeout = 2000;
	private static ZooKeeper zkClient = null;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);   //锁 ：阻塞程序     1 表表示初始值
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				System.out.println( "事件信息:" + event.getType()+",路径" + event.getPath() + ",状态" + event.getState());
				try {
					List<String> list = zkClient.getChildren("/", true);
					for (String s : list) {
						System.out.println(s);
					}
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				countDownLatch.countDown();  //阻塞值减一
			}
			
		});
		System.out.println("主程序阻塞....");
		countDownLatch.await();  // 归零之后停止阻塞
		System.out.println(zkClient);
	}
}
