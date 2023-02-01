package com.zkthinke.config;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;
import java.util.Map;

/**
 * @author weicb
 */
public class KeyTrackerServerFactory implements KeyedPooledObjectFactory<String, TrackerServer> {

	private Map<String,TrackerGroup> groupMap;

	public KeyTrackerServerFactory(Map<String,TrackerGroup> groupMap) {
		this.groupMap = groupMap;
	}

	public PooledObject<TrackerServer> wrap(TrackerServer value) {
		return new DefaultPooledObject<>(value);
	}

	@Override
	public PooledObject<TrackerServer> makeObject(String key) throws Exception {
		TrackerServer trackerServer =  new TrackerClient(groupMap.get(key)).getConnection();
		return new DefaultPooledObject<>(trackerServer);
	}

	@Override
	public void destroyObject(String key, PooledObject<TrackerServer> p) throws Exception {
		 p.getObject().close(); 
	}

	@Override
	public boolean validateObject(String key, PooledObject<TrackerServer> p) {
		try {
			return ProtoCommon.activeTest(p.getObject().getSocket());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void activateObject(String key, PooledObject<TrackerServer> p) throws Exception {
		
	}

	@Override
	public void passivateObject(String key, PooledObject<TrackerServer> p) throws Exception {
	
	}
}
