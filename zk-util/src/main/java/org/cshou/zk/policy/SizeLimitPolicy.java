/**
 * 
 */
package org.cshou.zk.policy;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.cshou.zk.intl.ContinuePolicy;

/**
 * @author cshou
 *
 */
public class SizeLimitPolicy implements ContinuePolicy {

	protected int size = 0;
	
	public SizeLimitPolicy (int size) {
		this.size = size;
	}
	
	@Override
	public boolean shouldContinue(ZooKeeper zk, String root) throws KeeperException, InterruptedException {
	
		List<String> children = zk.getChildren(root, true);
		
		if (children.size() < size && children.size() > 0)
			return true;
		
		return false;
	}

}
