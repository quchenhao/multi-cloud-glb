package glb.agent.core.dc;

public class DCStatus {

	private int capacity;
	
	private int load;
	
	private String dcId;
	
	DCStatus (String dcId) {
		this.dcId = dcId;
		this.capacity = 0;
		this.load = 0;;
	}
	
	public synchronized String toJSONString() {
		return "";
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
