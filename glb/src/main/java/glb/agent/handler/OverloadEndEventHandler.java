package glb.agent.handler;

import java.util.HashMap;
import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.event.Event;
import glb.agent.event.RedistributionEvent;

public class OverloadEndEventHandler extends EventHandler{

	@Override
	public Feedback handle(Event event) {
		LoadDistributionPlan loadDistributionPlan = new LoadDistributionPlan(new HashMap<String, Integer>(), 0);
		RedistributionEvent redistributionEvent = new RedistributionEvent(loadDistributionPlan);
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		synchronized(eventQueue) {
			eventQueue.add(redistributionEvent);
			eventQueue.notify();
		}
		
		return new Feedback(LocalDCStatusChangeLevel.NO_CHANGE);
	}
	
}
