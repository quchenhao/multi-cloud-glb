package glb.agent.core.dc;

import java.util.List;

public class LocalDCStatus extends DCStatus {
	
	private List<Integer> recentLoads;
	
	private int windowSize;
	
	public LocalDCStatus (String dcId, int windowSize) {
		super(dcId);
		this.windowSize = windowSize;
	}
	
	@Override
	public synchronized void update(int capacity, int load) {
		if (recentLoads.size() == windowSize) {
			recentLoads.remove(0);
		}
		
		recentLoads.add(load);
		
		this.capacity = capacity;
	}
	
	@Override
	public synchronized int getLoad() {
		return getMeanLoad();
	}

	public synchronized int getMeanLoad() {
		int sum = 0;
		for (Integer value : recentLoads) {
			sum += value;
		}
		
		return (int)Math.ceil((sum + 0.0)/recentLoads.size());
	}

	public synchronized int getMostRecentLoad() {
		return recentLoads.get(recentLoads.size() - 1);
	}
}
