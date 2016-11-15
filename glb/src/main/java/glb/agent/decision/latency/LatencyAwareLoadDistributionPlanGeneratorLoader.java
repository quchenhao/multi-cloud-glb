package glb.agent.decision.latency;

import java.util.Map;

import glb.agent.decision.LoadDistributionPlanGenerator;
import glb.agent.decision.LoadDistributionPlanGeneratorLoader;

public class LatencyAwareLoadDistributionPlanGeneratorLoader extends LoadDistributionPlanGeneratorLoader{

	@Override
	public LoadDistributionPlanGenerator load(Map<?, ?> parameters) {
		
		return new LatencyAwareLoadDistributionPlanGenerator();
	}

}
