/**
 * 
 */
package org.cshou.zk.membership;

import org.apache.zookeeper.ZooKeeper;

/**
 * @author cshou
 *
 */
public abstract class SubscriberHandler {
	
	public void notifyChange () {
		System.out.println("Not implemented for this class");
	}
	
	public void process (ZooKeeper zk, String root) {
		System.out.println("Not implemented for this class");
	}
	
}
