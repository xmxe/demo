package com.xmxe.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperWatch {
	private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

	public static void main(String[] args) {
		Thread t = new Thread(() -> {
			// 连接启动zk
			try {
				ZooKeeper zk = new ZooKeeper("192.168.236.133:2182", 60000, new Watcher() {
					// 创建默认的监听器
					public void process(WatchedEvent event) {
						System.out.println("changing...");
						System.out.println("state-->" + event.getState() + "  type-->" + event.getType());
						// 测试在连接zookeeper的时候想要执行监听器需要在连接后的代码加上await()阻塞一会,否则不会打印上面的语句,不知什么原理
					}
				});
				// 设置监听器 (before jdk1.8)
				/*
				Watcher wc = new Watcher() { 
					public void process(WatchedEvent event) {
				 		if (event.getType() == EventType.NodeDataChanged) {
							 System.out.println("change"); 
						} 
						if (event.getType() == EventType.NodeDeleted){ 
							System.out.println("dele"); 
						} 
						if (event.getType() == EventType.NodeCreated){ 
							System.out.println("create"); 
						} 
					}
				 
				};
				 */
				COUNT_DOWN_LATCH.await(2, TimeUnit.SECONDS);
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

				// 进行轮询,其中exists方法用来询问状态,并且设置了监听器,如果发生变化,则会回调监听器里的方法。如果不设置监听器,true为调用默认的监听器,false为不监听
				while (true) {
					zk.exists("/test", wc);
					System.out.println("------------");
					// zk.getData("/test", new ZooKeeperWatcher(), new Stat());
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t.start();
	}
}

class ZooKeeperWatcher implements Watcher {

	/** 定义原子变量 */
	AtomicInteger seq = new AtomicInteger();
	/** 定义session失效时间 */
	private static final int SESSION_TIMEOUT = 10000;
	/** zookeeper服务器地址 */
	// private static final String CONNECTION_ADDR = "192.168.1.121:2181,192.168.1.122:2181,192.168.1.123:2181";
	private static final String CONNECTION_ADDR = "192.168.236.133:2182";
	/** zk父路径设置 */
	private static final String PARENT_PATH = "/parent";
	/** zk子路径设置 */
	private static final String CHILDREN_PATH = "/parent/child";
	/** 进入标识 */
	private static final String LOG_PREFIX_OF_MAIN = "【Main】";
	/** zk变量 */
	private ZooKeeper zk = null;
	/** 用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行 */
	private CountDownLatch connectedSemaphore = new CountDownLatch(1);

	/**
	 * 创建ZK连接
	 * 
	 * @param connectAddr    ZK服务器地址列表
	 * @param sessionTimeout Session超时时间
	 */
	public void createConnection(String connectAddr, int sessionTimeout) {
		this.releaseConnection();
		try {
			// this表示把当前对象进行传递到其中去（也就是在主函数里实例化的new ZooKeeperWatcher()实例对象）
			zk = new ZooKeeper(connectAddr, sessionTimeout, this);
			System.out.println(LOG_PREFIX_OF_MAIN + "开始连接ZK服务器");
			connectedSemaphore.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭ZK连接
	 */
	public void releaseConnection() {
		if (this.zk != null) {
			try {
				this.zk.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建节点
	 * 
	 * @param path 节点路径
	 * @param data 数据内容
	 * @return
	 */
	public boolean createPath(String path, String data, boolean needWatch) {
		try {
			// 设置监控(由于zookeeper的监控都是一次性的所以 每次必须设置监控)
			this.zk.exists(path, needWatch);
			System.out.println(LOG_PREFIX_OF_MAIN + "节点创建成功, Path: " + this.zk.create( /** 路径 */
					path,
					/** 数据 */
					data.getBytes(),
					/** 所有可见 */
					Ids.OPEN_ACL_UNSAFE,
					/** 永久存储 */
					CreateMode.PERSISTENT) + ", content: " + data);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 读取指定节点数据内容
	 * 
	 * @param path 节点路径
	 * @return
	 */
	public String readData(String path, boolean needWatch) {
		try {
			System.out.println("读取数据操作...");
			return new String(this.zk.getData(path, needWatch, null));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 更新指定节点数据内容
	 * 
	 * @param path 节点路径
	 * @param data 数据内容
	 * @return
	 */
	public boolean writeData(String path, String data) {
		try {
			System.out.println(LOG_PREFIX_OF_MAIN + "更新数据成功,path：" + path + ", stat: "
					+ this.zk.setData(path, data.getBytes(), -1));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除指定节点
	 * 
	 * @param path 节点path
	 */
	public void deleteNode(String path) {
		try {
			this.zk.delete(path, -1);
			System.out.println(LOG_PREFIX_OF_MAIN + "删除节点成功,path：" + path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断指定节点是否存在
	 * 
	 * @param path 节点路径
	 */
	public Stat exists(String path, boolean needWatch) {
		try {
			return this.zk.exists(path, needWatch);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取子节点
	 * 
	 * @param path 节点路径
	 */
	private List<String> getChildren(String path, boolean needWatch) {
		try {
			System.out.println("读取子节点操作...");
			return this.zk.getChildren(path, needWatch);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 删除所有节点
	 */
	public void deleteAllTestPath(boolean needWatch) {
		if (this.exists(CHILDREN_PATH, needWatch) != null) {
			this.deleteNode(CHILDREN_PATH);
		}
		if (this.exists(PARENT_PATH, needWatch) != null) {
			this.deleteNode(PARENT_PATH);
		}
	}

	/**
	 * 收到来自Server的Watcher通知后的处理。
	 */
	@Override
	public void process(WatchedEvent event) {

		System.out.println("进入 process 。。。。。event = " + event);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (event == null) {
			return;
		}

		// 连接状态
		KeeperState keeperState = event.getState();
		// 事件类型
		EventType eventType = event.getType();
		// 受影响的path
		String path = event.getPath();
		// 原子对象seq 记录进入process的次数
		String logPrefix = "【Watcher-" + this.seq.incrementAndGet() + "】";

		System.out.println(logPrefix + "收到Watcher通知");
		System.out.println(logPrefix + "连接状态:\t" + keeperState.toString());
		System.out.println(logPrefix + "事件类型:\t" + eventType.toString());

		if (KeeperState.SyncConnected == keeperState) {
			// 成功连接上ZK服务器
			if (EventType.None == eventType) {
				System.out.println(logPrefix + "成功连接上ZK服务器");
				connectedSemaphore.countDown();
			}
			// 创建节点
			else if (EventType.NodeCreated == eventType) {
				System.out.println(logPrefix + "节点创建");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 更新节点
			else if (EventType.NodeDataChanged == eventType) {
				System.out.println(logPrefix + "节点数据更新");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 更新子节点
			else if (EventType.NodeChildrenChanged == eventType) {
				System.out.println(logPrefix + "子节点变更");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 删除节点
			else if (EventType.NodeDeleted == eventType) {
				System.out.println(logPrefix + "节点 " + path + " 被删除");
			} else
				;
		} else if (KeeperState.Disconnected == keeperState) {
			System.out.println(logPrefix + "与ZK服务器断开连接");
		} else if (KeeperState.AuthFailed == keeperState) {
			System.out.println(logPrefix + "权限检查失败");
		} else if (KeeperState.Expired == keeperState) {
			System.out.println(logPrefix + "会话失效");
		} else
			;

		System.out.println("--------------------------------------------");

	}

	public static void main(String[] args) throws InterruptedException {
		// 建立watcher //当前客户端可以称为一个watcher 观察者角色
		ZooKeeperWatcher zkWatch = new ZooKeeperWatcher();
		// 创建连接
		zkWatch.createConnection(CONNECTION_ADDR, SESSION_TIMEOUT);
		// System.out.println(zkWatch.zk.toString());

		Thread.sleep(1000);

		// 清理节点
		// zkWatch.deleteAllTestPath(false);

		// -----------------第一步: 创建父节点 /parent ------------------------//
		if (zkWatch.createPath(PARENT_PATH, System.currentTimeMillis() + "", true)) {

			Thread.sleep(1000);

			// -----------------第二步: 读取节点 /parent 和 读取/parent节点下的子节点(getChildren)的区别
			// 读取数据
			String readData = zkWatch.readData(PARENT_PATH, true);
			System.out.println("readData=" + readData);

			// 读取子节点(监控childNodeChange事件)
			List<String> children = zkWatch.getChildren(PARENT_PATH, true);
			children.forEach(xhild -> System.out.println(xhild));

			// 更新数据
			zkWatch.writeData(PARENT_PATH, System.currentTimeMillis() + "");

			Thread.sleep(1000);
			// 创建子节点
			zkWatch.createPath(CHILDREN_PATH, System.currentTimeMillis() + "", true);

			// -----------------第三步: 建立子节点的触发 --------------//
			// zkWatch.createPath(CHILDREN_PATH + "/c1", System.currentTimeMillis() + "",
			// true);
			// zkWatch.createPath(CHILDREN_PATH + "/c1/c2", System.currentTimeMillis() + "",
			// true);

			// -----------------第四步: 更新子节点数据的触发 --------------//
			// 在进行修改之前,我们需要watch一下这个节点：
			Thread.sleep(1000);
			zkWatch.readData(CHILDREN_PATH, true);
			zkWatch.writeData(CHILDREN_PATH, System.currentTimeMillis() + "");

		}

		Thread.sleep(10000);
		// 清理节点
		zkWatch.deleteAllTestPath(false);

		Thread.sleep(10000);
		zkWatch.releaseConnection();

	}

}
