package glb.agent.core.dc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCStatus {

	protected int capacity;
	
	protected int totalLoad;
	
	private String dcId;
	
	protected Map<String, Integer> outSourcedLoad;
	
	DCStatus (String dcId) {
		this.dcId = dcId;
		this.capacity = 0;
		this.totalLoad = 0;
		this.outSourcedLoad = new HashMap<String, Integer>();
	}
	
	public synchronized int getCapacity() {
		return capacity;
	}
	
	public synchronized int getTotalLoad() {
		return totalLoad;
	}
	
	public String getDCId() {
		return dcId;
	}
	
	public synchronized void update(int capacity, int load, Map<String, Integer> outSourcedLoad) {
		
		updateCapacity(capacity);
		updateLoad(load);
		updateOutSourcedLoad(outSourcedLoad);
	}
	
	public synchronized void updateCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public synchronized void updateLoad(int load) {
		this.totalLoad = load;
	}
	
	public synchronized void updateOutSourcedLoad(Map<String, Integer> outSourcedLoad) {
		if (outSourcedLoad != null) {
			this.outSourcedLoad.clear();
			if (!outSourcedLoad.isEmpty()) {
				this.outSourcedLoad.putAll(outSourcedLoad);
			}
		}
	}
	
	public synchronized Map<String, Integer> getOutsourcedLoad() {
		return Collections.unmodifiableMap(outSourcedLoad);
	}
}
