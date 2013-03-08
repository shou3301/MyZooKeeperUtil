/**
 * 
 */
package org.cshou.zk.membership;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author cshou
 *
 */
public class SubscriberBase implements Watcher {

	protected ZooKeeper zk = null;
	
	protected String root = null;
	
	protected SubscriberHandler handler = null;
	
	// protected Boolean mutex;
	
	protected SubscribeType way = null;
	
	public SubscriberBase (ZooKeeper zk, String root, 
			SubscriberHandler handler, SubscribeType way) {
		this.zk = zk;
		this.root = root;
		this.handler = handler;
		this.way = way;
	}

	@Override
	public void process(WatchedEvent event) {
		EventType et = event.getType();
		if (et == EventType.NodeChildrenChanged ||
				et == EventType.NodeDataChanged) {
			
			switch (way) {
			
				case PULL:
					handler.notifyChange();
					break;
					
				case PUSH:
					handler.process(zk, root);
					break;
					
				default:
					break;
			}
		}
		
		try {
			zk.exists(root, true);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean register () throws KeeperException, InterruptedException {
		Stat stat = zk.exists(root, true);
		return (stat == null) ? false : true;
	}

	public SubscriberHandler getHandler() {
		return handler;
	}

	public void setHandler(SubscriberHandler handler) {
		this.handler = handler;
	}
	
}
