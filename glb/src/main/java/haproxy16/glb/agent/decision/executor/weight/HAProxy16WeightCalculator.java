package haproxy16.glb.agent.decision.executor.weight;

import java.util.Collection;
import java.util.Map;

import glb.agent.core.dc.Server;

public abstract class HAProxy16WeightCalculator {

	public abstract WeightTable calculateWeight(Collection<Server> healthyServers, Map<String, Integer> outSourcedLoad);
}
