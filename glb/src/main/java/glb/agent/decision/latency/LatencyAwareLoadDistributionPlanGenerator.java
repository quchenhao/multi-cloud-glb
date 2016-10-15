package glb.agent.decision.latency;

import java.util.Collection;

import glb.agent.core.dc.DCStatus;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.decision.LoadDistributionPlanGenerator;

public class LatencyAwareLoadDistributionPlanGenerator extends LoadDistributionPlanGenerator{

	@Override
	public LoadDistributionPlan generateOverloadHandlingPlan(LocalDCStatus localDCStatus,
			Collection<DCStatus> remoteDCStatuses) {
		
		return null;
	}

}
