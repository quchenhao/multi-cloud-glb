package glb.agent.handler;

import glb.agent.decision.OverloadHandlingPlan;
import glb.agent.event.Event;
import glb.agent.event.RedistributionEvent;

public abstract class RedistributionEventHandler extends EventHandler{
	
	@Override
	public void handle(Event event) {
		RedistributionEvent redistributionEvent = (RedistributionEvent)event;
		
		redistribute(redistributionEvent.getOverloadHandlingPlan());
	}

	protected abstract void redistribute(OverloadHandlingPlan overloadHandlingPlan);
}
