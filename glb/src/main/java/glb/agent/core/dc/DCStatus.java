package glb.agent.core.dc;

import org.json.simple.JSONObject;

public class DCStatus {

	protected int capacity;
	
	protected int load;
	
	private String dcId;
	
	DCStatus (String dcId) {
		this.dcId = dcId;
		this.capacity = 0;
		this.load = 0;;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized String toJSONString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("capacity", getCapacity());
		jsonObject.put("load", getLoad());
		
		return jsonObject.toJSONString();
	}
	
	
	public synchronized int getCapacity() {
		return capacity;
	}
	
	public synchronized int getLoad() {
		return load;
	}
	
	public synchronized int getSpareCapacity() {
		if (capacity > load) {
			return capacity - load;
		}
		
		return 0;
	}
	
	public String getDCId() {
		return dcId;
	}
	
	synchronized void update(int capacity, int load) {
		this.capacity = capacity;
		this.load = load;
	}
	
	
}
