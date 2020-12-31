package cn.kgc.zookeeper.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;

public class ZookeeperAuth1 {
	final static String CONNECT_ADDR = "192.168.10.11:2181";
	static final int SESSION_OUTTIME = 2000;//ms 
	
	static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws Exception {
		ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				//获取事件的状态
				KeeperState keeperState = event.getState();
				EventType eventType = event.getType();
				//如果是建立连接
				if(KeeperState.SyncConnected == keeperState){
					if(EventType.None == eventType){
						//如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
						connectedSemaphore.countDown();
						System.out.println("zk 建立连接");
					}
				}
			}
			
		});
		
		zk.addAuthInfo("digest","321".getBytes());
		connectedSemaphore.await();
		
		List<ACL> acls = new ArrayList<ACL>(1);
		for (ACL ids_acl : Ids.CREATOR_ALL_ACL) {
			acls.add(ids_acl);
		}
//		
//		zk.create("/bb", "init content".getBytes(), acls, CreateMode.PERSISTENT);
		System.out.println(new String(zk.getData("/bb", null, null)));
		
		
//		ZooKeeper nozk = new ZooKeeper(CONNECT_ADDR, 2000, null);
//		Thread.sleep(10000);
//		System.out.println( "成功获取数据：" + nozk.getData("/bb", false, null));
		
	}
	

}
