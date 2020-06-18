package com.xmxe.study_demo.kafka;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperWatchTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread t = new Thread(new Wa());
		t.start();
	}

}

class Wa implements Runnable {

	@Override
	public void run() {
		// 连接启动k
		try {
			ZooKeeper zk = new ZooKeeper("192.168.236.128:2181", 60000, new Watcher() {
				// 监控所有被触发的事件
				public void process(WatchedEvent event) {
					System.out.println("changing...");
				}
			});
			// 设置监听器
		/*	Watcher wc = new Watcher() {
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if (event.getType() == EventType.NodeDataChanged) {
						System.out.println("change");
					}
					if (event.getType() == EventType.NodeDeleted) {
						System.out.println("dele");
					}
					if (event.getType() == EventType.NodeCreated) {
						System.out.println("create");
					}
				}

			};*/
			
			Watcher wc = (event) -> {
				if (event.getType() == EventType.NodeDataChanged) {
					System.out.println("change");
				}
				if (event.getType() == EventType.NodeDeleted) {
					System.out.println("dele");
				}
				if (event.getType() == EventType.NodeCreated) {
					System.out.println("create");
				}
				
			};
			// 进行轮询，其中exists方法用来询问状态，并且设置了监听器，如果发生变化，则会回调监听器里的方法。
			while (true) {
				zk.exists("/test", wc);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}