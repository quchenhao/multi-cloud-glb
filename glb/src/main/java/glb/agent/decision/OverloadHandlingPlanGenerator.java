package glb.agent.decision;

import java.util.Collection;

import glb.agent.core.dc.DCStatus;
import glb.agent.core.dc.LocalDCStatus;

public abstract class OverloadHandlingPlanGenerator {

	public abstract OverloadHandlingPlan generateOverloadHandlingPlan(LocalDCStatus localDCStatus, Collection<DCStatus> remoteDCStatuses);
}
