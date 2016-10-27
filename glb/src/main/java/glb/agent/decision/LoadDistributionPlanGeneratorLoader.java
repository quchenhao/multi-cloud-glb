package glb.agent.decision;

import java.util.Map;

public abstract class LoadDistributionPlanGeneratorLoader {
	public abstract LoadDistributionPlanGenerator load(Map<?, ?> parameters);
}
