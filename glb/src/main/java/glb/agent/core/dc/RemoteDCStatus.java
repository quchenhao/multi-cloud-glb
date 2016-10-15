package glb.agent.core.dc;

import java.util.Map;

public class RemoteDCStatus extends DCStatus {
	private int maxServiceRate;
	private int capacity;
	private int latency;
	private int totalLoad;
	
	public RemoteDCStatus(String dcId, int latency) {
		super(dcId);
		this.latency = latency;
		this.totalLoad = 0;
	}
	
	public synchronized int getLatency() {
		return latency;
	}
	
	public synchronized void updateLatency(int latency) {
		this.latency = latency;
	}

	@Override
	public synchronized void updateLoad(int load) {
		this.totalLoad = load;
	}
	
	@Override
	public synchronized int getTotalLoad() {
		return totalLoad;
	}
	
	@Override
	public synchronized int getCapacity() {
		return capacity;
	}
	
	public synchronized void updateCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public synchronized int getMaxServiceRate() {
		return maxServiceRate;
	}
	
	public synchronized void updateMaxServiceRate(int maxServiceRate) {
		this.maxServiceRate = maxServiceRate;
	}
	
	public synchronized void update(int maxServiceRate, int capacity, int load, Map<String, Integer> outSourcedLoad) {
		
		updateMaxServiceRate(maxServiceRate);
		updateCapacity(capacity);
		updateLoad(load);
		updateOutSourcedLoad(outSourcedLoad);
	}
}
