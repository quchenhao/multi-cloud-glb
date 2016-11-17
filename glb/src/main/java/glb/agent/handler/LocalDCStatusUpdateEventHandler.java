package glb.agent.handler;

import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.detector.OverloadDetector;
import glb.agent.event.Event;
import glb.agent.event.OverloadEndEvent;
import glb.agent.event.OverloadEvent;

public class LocalDCStatusUpdateEventHandler extends EventHandler {
	
	private OverloadDetector overloadDetector;
	
	public LocalDCStatusUpdateEventHandler(OverloadDetector overloadDetector) {
		this.overloadDetector = overloadDetector;
	}

	@Override
	public Feedback handle(Event event) {
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		if (overloadDetector.isOverload(localDCStatus)) {
			OverloadEvent overloadEvent = new OverloadEvent();
			
			synchronized(eventQueue) {
				eventQueue.add(overloadEvent);
			}
		}
		else if (!localDCStatus.getOutsourcedLoad().isEmpty()) {
			OverloadEndEvent overloadEndEvent = new OverloadEndEvent();
			
			synchronized(eventQueue) {
				eventQueue.add(overloadEndEvent);
				eventQueue.notify();
			}
		}
		
		return new Feedback(LocalDCStatusChangeLevel.NO_CHANGE);
	}

}
