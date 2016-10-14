package glb.agent.decision.weight;

import java.util.Map;

public class WeightTable {

	private Map<String, Integer> serverWeights;
	private Map<String, Integer> outSourcedWeights;
	
	public WeightTable(Map<String, Integer> serverWeights, Map<String, Integer> outSourcedWeights) {
		this.serverWeights = serverWeights;
		this.outSourcedWeights = outSourcedWeights;
	}
	
	public int getServerWeight(String serverId) {
		if (serverWeights.containsKey(serverId)) {
			return serverWeights.get(serverId);
		}
		
		return 0;
	}
	
	public int getRemoteDCWeight(String dcId) {
		if (outSourcedWeights.containsKey(dcId)) {
			return outSourcedWeights.get(dcId);
		}
		
		return 0;
	}
}
