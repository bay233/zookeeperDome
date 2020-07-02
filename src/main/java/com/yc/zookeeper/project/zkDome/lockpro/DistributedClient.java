package com.yc.zookeeper.project.zkDome.lockpro;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 分布式锁的应用
 */
public class DistributedClient {
	private static final String root = "/test/root"; // 父节点
	private static final String lock = "lock_"; // 定义一个节点名头
	private String thisPath;
	private static final int sessionTimeout = 5000; // 会话时间
	private String connectString = "node1:2181,node1:2181,node1:2181"; // zk所在的地址
	private ZooKeeper zooKeeper;

	// 节点拿到锁后执行的任务
	public void doTask() throws InterruptedException, KeeperException {
		System.out.println(Thread.currentThread().getName() + "获取到锁，开始执行任务...");
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			zooKeeper.delete(root + "/" + this.thisPath, -1);
		}
		System.out.println(Thread.currentThread().getName() + "任务执行完毕，解除锁...");
	}

	public void lock() {
		try {
			// 创建zk连接，并设置监听事件
			zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						if (event.getType() == Event.EventType.NodeDeleted) {
							System.out.println("有节点" + event.getPath() + ":被删除了");
							try {
								// 获取父节点下所有的子节点并进行排序，
								// 获取序列号最小的节点
								List<String> children1 = zooKeeper.getChildren(root, false);
								Collections.sort(children1);
								int index = children1.indexOf(thisPath);
								// 如果最小节点是当前节点则执行任务
								if (index == 0) {
									doTask();
								} 
							} catch (KeeperException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			// 创建临时序列化节点
			this.thisPath = zooKeeper.create(root + "/" + lock, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println(Thread.currentThread().getName() + "create " + this.thisPath);
			this.thisPath = thisPath.substring(thisPath.lastIndexOf("/") + 1);
			List<String> children = zooKeeper.getChildren(root, false);
			// 如果父节点下只有一个子节点，则此节点直至获取锁
			if (children.size() == 1) {
				if (children.get(0).equals(this.thisPath)) {
					doTask();
				}
			} else {
				// 获取所有兄弟节点，如果所有兄弟节点中当前的序列号最小则执行任务
				Collections.sort(children);
				if (children.get(0).equals(this.thisPath)) {
					doTask();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					DistributedClient c = new DistributedClient();
					try {
						Thread.sleep(new Random().nextInt(1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					c.lock();
				}
			}).start();
		}
		Thread.sleep(60000);
	}

}
