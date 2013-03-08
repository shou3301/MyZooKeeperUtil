/**
 * 
 */
package org.cshou.zk.membership;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author cshou
 *
 */
public class ReleaserBase {
	
	protected ZooKeeper zk = null;
	
	protected String root = null;
	
	protected String key = null;
	
	protected String baseDir = null;
	
	// protected static int version = 0;
	
	public ReleaserBase (ZooKeeper zk, String root, String key) {
		
		this.zk = zk;
		this.root = root;
		this.key = key;
		this.baseDir = this.root + "/" + this.key;
		
		// create key znode
		if (zk != null) {
			try {
				
				Stat stat = zk.exists(baseDir, false);
				
				if (stat == null) {
					zk.create(baseDir, null, Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean update (String attr, Object value) throws KeeperException, InterruptedException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] data = null;
		
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(value);
			data = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (null == zk.exists(baseDir + "/" + attr, false))
			zk.create(baseDir + "/" + attr, data, Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);
		else {
			zk.setData(baseDir + "/" + attr, data, -1);
		}
		
		return true;
	}
	
}
