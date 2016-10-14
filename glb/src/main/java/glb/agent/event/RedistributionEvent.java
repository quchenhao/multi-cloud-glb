package glb.agent.event;

import glb.agent.decision.OverloadHandlingPlan;

public class RedistributionEvent extends Event {

	private OverloadHandlingPlan overloadHandlingPlan;
	
	public RedistributionEvent(OverloadHandlingPlan overloadHandlingPlan) {
		super("RedistributionEvent");
		this.overloadHandlingPlan = overloadHandlingPlan;
	}
	
	public OverloadHandlingPlan getOverloadHandlingPlan() {
		return overloadHandlingPlan;
	}

}
