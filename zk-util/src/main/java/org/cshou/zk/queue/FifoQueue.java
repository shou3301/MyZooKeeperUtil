/**
 * 
 */
package org.cshou.zk.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.cshou.zk.intl.SyncPrimitive;

/**
 * @author cshou
 *
 */
public class FifoQueue extends SyncPrimitive {

	private QueueHandler handler = null;
	
	protected boolean returnable = false;
	
	protected boolean oneTime = true;
	
	public FifoQueue (ZooKeeper zk, String root, QueueHandler handler,
			boolean returnable, boolean oneTime) {
		this.zk = zk;
		this.root = root;
		this.setHandler(handler);
		this.returnable = returnable;
		this.oneTime = oneTime;
	}
	
	@Override
	public boolean add(String name, Object value) throws KeeperException,
			InterruptedException {
		
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
		
		zk.create(root + "/" + name + "-", data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		
		return true;
	}

	@Override
	public boolean remove(String name) throws KeeperException,
			InterruptedException {
		
		Stat stat = null;
		
		while (true) {
			synchronized (mutex) {
				
				List<String> children = zk.getChildren(root, true);
				
				if (children.size() > 0) {
					
					int min = Integer.MAX_VALUE;
					
					for (String s : children) {
						String[] params = s.split("-");
						int seq = Integer.parseInt(params[1]);
						if (seq < min)
							min = seq;
					}
					
					byte[] bytes = zk.getData(root + "/" + name + "-" + min, false, stat);
					zk.delete(root + "/" + name + "-" + min, 0);
					
					ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
					ObjectInput in = null;
					Object object = null;
					
					try {
						in = new ObjectInputStream(bis);
						object = in.readObject(); 
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							bis.close();
						  	in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					this.getHandler().process(object);
					
					if (this.oneTime)
						return true;
					
				}
				else if (returnable) {
					return true;
				}
				else {
					mutex.wait();
				}
			}
		}
		
	}
	
	public void setReturnable (boolean flag) {
		this.returnable = flag;
	}

	public QueueHandler getHandler() {
		return handler;
	}

	public void setHandler(QueueHandler handler) {
		this.handler = handler;
	}

}
