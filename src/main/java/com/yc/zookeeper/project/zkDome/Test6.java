package com.yc.zookeeper.project.zkDome;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * 以树状形式显示所有节点信息
 */
public class Test6 {
	private static ZkHelper helper;
	private static ZooKeeper zk;

	public static Stat znode_exists(String path) throws KeeperException, InterruptedException {
		return zk.exists(path, true);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String root = "/";
		helper = new ZkHelper();
		zk = helper.connect();
		showTree(root,0);
		helper.close();
	}

	private static void showTree(String path, int level) {
		Stat stat = null;
		try {
			stat = znode_exists(path);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			sb.append("    ");
		}
		System.out.print(sb.toString() + path);
		
		List<String> children = null;
		try {
			children = zk.getChildren(path, false);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("*无权限");
			return ;
		}
		System.out.println();
		for (int i = 0; i < children.size(); i ++){
			String sonPath = children.get(i);
			if (level == 0){
				showTree(path+sonPath, level+1);
			}else{
				showTree(path+"/"+sonPath, level+1);
			}
		}
	}
}
