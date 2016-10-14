package glb.agent.decision.weight.haproxy16;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glb.agent.core.dc.Server;
import glb.agent.decision.weight.WeightCalculator;
import glb.agent.decision.weight.WeightTable;

public class SimpleHAProxy16WeightCalculator extends WeightCalculator {

	@Override
	public WeightTable calculateWeight(Collection<Server> healthyServers, Map<String, Integer> outSourcedLoad) {
		
		int max = 0;
		
		for (Server server : healthyServers) {
			int capacity = server.getCapacity();
			if (capacity > max) {
				max = capacity;
			}
		}
		
		for (Integer load : outSourcedLoad.values()) {
			if (load > max) {
				max = load;
			}
		}
		
		int scaler = 2;
		
		if (max > 256) {
			while (max/scaler > 256) {
				scaler++;
			}
		}
		
		Map<String, Integer> serverWeights = new HashMap<String, Integer>();
		
		for () {}
		
		return null;
	}

}
