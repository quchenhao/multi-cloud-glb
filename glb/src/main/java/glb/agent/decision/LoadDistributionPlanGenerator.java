package glb.agent.decision;

import java.util.Collection;

import glb.agent.core.dc.DCStatus;
import glb.agent.core.dc.LocalDCStatus;

public abstract class LoadDistributionPlanGenerator {

	public abstract LoadDistributionPlan generateOverloadHandlingPlan(LocalDCStatus localDCStatus, Collection<DCStatus> remoteDCStatuses);
}
