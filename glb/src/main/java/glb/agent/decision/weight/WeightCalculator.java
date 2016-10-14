package glb.agent.decision.weight;

import java.util.Collection;
import java.util.Map;

import glb.agent.core.dc.Server;

public abstract class WeightCalculator {

	public abstract WeightTable calculateWeight(Collection<Server> healthyServers, Map<String, Integer> outSourcedLoad);
}
