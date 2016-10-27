package glb.agent.monitor;

import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.event.Event;
import glb.agent.event.LocalDCStatusUpdateEvent;

public abstract class LoadMonitor extends Monitor{

	public void monitor() throws Exception {
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		int load = getLoad();
		localDCStatus.updateLoad(load);
		LocalDCStatusUpdateEvent event = new LocalDCStatusUpdateEvent();
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		eventQueue.add(event);
		eventQueue.notifyAll();
	}
	
	protected abstract int getLoad() throws Exception;
	public abstract void calibrate(int adjust);
}
