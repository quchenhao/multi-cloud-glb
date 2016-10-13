package glb.agent.core.dc;

import java.util.List;
import java.util.Map;

public class LocalDCStatus extends DCStatus {
	
	private List<Integer> recentLoads;
	
	private int windowSize;
	
	public LocalDCStatus (String dcId, int windowSize) {
		super(dcId);
		this.windowSize = windowSize;
	}
	
	@Override
	public synchronized void update(int capacity, int load, Map<String, Integer> outSourcedLoad) {
		if (recentLoads.size() == windowSize) {
			recentLoads.remove(0);
		}
		
		recentLoads.add(load);
		
		if (outSourcedLoad != null) {
			this.outSourcedLoad = outSourcedLoad;
		}
		
		
		this.capacity = capacity;
	}
	
	@Override
	public synchronized int getTotalLoad() {
		return getMostRecentLoad();
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
