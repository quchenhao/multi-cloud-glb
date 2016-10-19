package haproxy16.glb.agent.handler.weight;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import glb.agent.core.dc.Server;
import glb.agent.handler.weight.WeightCalculator;
import glb.agent.handler.weight.WeightTable;

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
		
		int scaler = 1;
		
		while (max/scaler > 256) {
			scaler++;
		}
		
		Map<String, Integer> serverWeights = new HashMap<String, Integer>();
		
		for (Server server : healthyServers) {
			serverWeights.put(server.getServerId(), server.getCapacity()/scaler);
		}
		
		Map<String, Integer> outSourcedWeights = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : outSourcedLoad.entrySet()) {
			outSourcedWeights.put(entry.getKey(), entry.getValue()/scaler);
		}
		
		return new WeightTable(serverWeights, outSourcedWeights);
	}

}
