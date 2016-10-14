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
	public void handle(Event event) {
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		if (overloadDetector.isOverload(localDCStatus)) {
			OverloadEvent overloadEvent = new OverloadEvent();
			eventQueue.add(overloadEvent);
			eventQueue.notifyAll();
		}
		else if (!localDCStatus.getOutsourcedLoad().isEmpty()) {
			OverloadEndEvent overloadEndEvent = new OverloadEndEvent();
			eventQueue.add(overloadEndEvent);
			eventQueue.notifyAll();
		}
	}

}
