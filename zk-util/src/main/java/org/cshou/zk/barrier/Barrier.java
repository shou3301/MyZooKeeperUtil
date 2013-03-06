/**
 * 
 */
package org.cshou.zk.barrier;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.cshou.zk.intl.ContinuePolicy;
import org.cshou.zk.intl.SyncPrimitive;

/**
 * @author cshou
 *
 */
public class Barrier extends SyncPrimitive {

	protected int size = 0;
	
	protected String root = null;
	
	protected ContinuePolicy addPolicy = null;
	
	protected ContinuePolicy removePolicy = null;
	
	public Barrier (ZooKeeper zk, String root, int size, 
			ContinuePolicy addPolicy, ContinuePolicy removePolicy) {
		this.zk = zk;
		this.root = root;
		this.size = size;
		this.addPolicy = addPolicy;
		this.removePolicy = removePolicy;
	}

	@Override
	public boolean add (String name) throws KeeperException, InterruptedException {
		
		zk.create(root + "/" + name, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		
		while (true) {
			synchronized (mutex) {
				
				if (addPolicy.shouldContinue(zk, root)) {
					mutex.wait();
				}
				else {
					return true;
				}
			}
		}
		
	}

	@Override
	public boolean remove (String name) throws KeeperException, InterruptedException {
		
		zk.delete(root + "/" + name, 0);
		
		while (true) {
			synchronized (mutex) {
				
				if (removePolicy.shouldContinue(zk, root)) {
					mutex.wait();
				}
				else {
					return true;
				}
			}
		}
		
	}
	
}
