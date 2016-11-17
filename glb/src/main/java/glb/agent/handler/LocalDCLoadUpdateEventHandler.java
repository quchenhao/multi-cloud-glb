package glb.agent.handler;

import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.event.Event;
import glb.agent.event.LocalDCLoadUpdateEvent;
import glb.agent.event.LocalDCStatusUpdateEvent;

public class LocalDCLoadUpdateEventHandler extends EventHandler{

	private double ratioThreshold = 0.1;
	
	@Override
	public Feedback handle(Event event) {
		LocalDCLoadUpdateEvent localDCLoadUpdateEvent = (LocalDCLoadUpdateEvent)event;
		int load = localDCLoadUpdateEvent.getLoad();
		
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		
		int oldLoad = localDCStatus.getMostRecentLoad();
		
		localDCStatus.updateLoad(load);
		
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		synchronized(eventQueue) {
			LocalDCStatusUpdateEvent localDCStatusUpdateEvent = new LocalDCStatusUpdateEvent();
			eventQueue.add(localDCStatusUpdateEvent);
			eventQueue.notify();
		}
		
		if ((load - oldLoad + 0.0)/oldLoad > ratioThreshold) {
			return new Feedback(LocalDCStatusChangeLevel.NOTABLE_CHANGE);
		}
		else if (load != oldLoad) {
			return new Feedback(LocalDCStatusChangeLevel.SMALL_CHANGE);
		}
		else {
			return new Feedback(LocalDCStatusChangeLevel.NOTABLE_CHANGE);
		}
	}

	public void setRatioThreshold(double threshold) {
		if (threshold < 0) {
			throw new IllegalArgumentException("threshold must be positive");
		}
		
		this.ratioThreshold = threshold;
	}
}
