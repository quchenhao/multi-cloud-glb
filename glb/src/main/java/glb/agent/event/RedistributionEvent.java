package glb.agent.event;

import glb.agent.decision.LoadDistributionPlan;

public class RedistributionEvent extends Event {

	private LoadDistributionPlan overloadHandlingPlan;
	
	public RedistributionEvent(LoadDistributionPlan overloadHandlingPlan) {
		super(EventType.RedistributionEvent);
		this.overloadHandlingPlan = overloadHandlingPlan;
	}
	
	public LoadDistributionPlan getRedistributionPlan() {
		return overloadHandlingPlan;
	}

}
