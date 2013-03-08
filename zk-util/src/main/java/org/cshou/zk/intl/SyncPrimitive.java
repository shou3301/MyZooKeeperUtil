/**
 * 
 */
package org.cshou.zk.intl;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author cshou
 *
 */
public abstract class SyncPrimitive implements Watcher {

	protected ZooKeeper zk = null;
	protected String root = null;
	protected Boolean mutex;

	
	@Override
	public void process(WatchedEvent event) {
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}
	
	public abstract boolean add (String name, Object value) throws KeeperException, InterruptedException;
	
	public abstract boolean remove (String name) throws KeeperException, InterruptedException;

}
