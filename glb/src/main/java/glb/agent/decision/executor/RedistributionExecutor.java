package glb.agent.decision.executor;

import glb.agent.decision.LoadDistributionPlan;

public abstract class RedistributionExecutor {

	public abstract void redistribute(LoadDistributionPlan loadDistributionPlan) throws Exception;
}
