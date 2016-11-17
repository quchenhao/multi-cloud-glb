package glb.agent.handler;

import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.DCStatus;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.event.Event;
import glb.agent.event.OverloadEvent;
import glb.agent.event.RemoteDCStatusUpdateEvent;

public class RemoteDCStatusUpdateHandler extends EventHandler {

	@Override
	public Feedback handle(Event event) {
		RemoteDCStatusUpdateEvent remoteDCStatusUpdateEvent = (RemoteDCStatusUpdateEvent)event;
		String remoteDCId = remoteDCStatusUpdateEvent.getDCId();
		
		DCManager dcManager = DCManager.getDCManager();
		DCStatus remoteDCStatus = dcManager.getRemoteDCStatus(remoteDCId);
		
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		
		if (remoteDCStatus.getCapacity() <= remoteDCStatus.getTotalLoad() && localDCStatus.getOutsourcedLoad().containsKey(remoteDCId)) {
			OverloadEvent overloadEvent = new OverloadEvent();
			Queue<Event> eventQueue = EventQueue.getEventQueue();
			
			synchronized(eventQueue) {
				eventQueue.add(overloadEvent);
				eventQueue.notify();
			}
		}
		
		return new Feedback(LocalDCStatusChangeLevel.NO_CHANGE);
	}

}
