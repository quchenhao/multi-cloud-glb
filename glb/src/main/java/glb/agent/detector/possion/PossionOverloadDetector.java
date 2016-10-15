package glb.agent.detector.possion;

import java.util.List;

import glb.agent.core.dc.LocalDCStatus;
import glb.agent.detector.OverloadDetector;

public class PossionOverloadDetector extends OverloadDetector {

	@Override
	public boolean isOverload(LocalDCStatus localDCStatus) {
		int capacity = localDCStatus.getCapacity();
		int threshold = (int)Math.floor(Math.sqrt(capacity) + capacity);
		int load = localDCStatus.getMostRecentLoad();
		if (load > threshold) {
			return true;
		}
		else if (load <= capacity) {
			return false;
		}
		
		List<Integer> recentLoads = localDCStatus.getRecentLoads();
		
		for (Integer integer : recentLoads) {
			if (integer <= capacity) {
				return false;
			}
		}
		
		return true;
	}

}
