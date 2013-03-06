/**
 * 
 */
package org.cshou.zk.intl;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author cshou
 *
 */
public interface ContinuePolicy {
	
	public boolean shouldContinue (ZooKeeper zk, String root) throws KeeperException, InterruptedException;
	
}
