package glb.agent.decision;

import java.util.Collection;

import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.RemoteDCStatus;

public abstract class LoadDistributionPlanGenerator {

	public abstract LoadDistributionPlan generateOverloadHandlingPlan(LocalDCStatus localDCStatus, Collection<RemoteDCStatus> remoteDCStatuses) throws Exception;
}
