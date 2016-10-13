package glb.agent.core.dc;

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
		this.capacity = capacity;
		this.totalLoad = load;
		if (outSourcedLoad != null) {
			this.outSourcedLoad = outSourcedLoad;
		}
	}
	
	public synchronized Map<String, Integer> getOutsourcedLoad() {
		return outSourcedLoad;
	}
}
