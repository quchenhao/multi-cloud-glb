package glb.agent.monitor;

import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.event.Event;
import glb.agent.event.LocalDCLoadUpdateEvent;

public abstract class LoadMonitor extends Monitor{
	
	public LoadMonitor() {
	}

	public void monitor() throws Exception {
		int load = getLoad();
		LocalDCLoadUpdateEvent event = new LocalDCLoadUpdateEvent(load);
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		synchronized(eventQueue) {
			eventQueue.add(event);
			eventQueue.notifyAll();
		}
	}
	
	protected abstract int getLoad() throws Exception;
	public abstract void calibrate(int adjust);
}
