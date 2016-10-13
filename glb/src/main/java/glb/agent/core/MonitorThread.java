package glb.agent.core;

import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.monitor.Measure;
import glb.agent.monitor.Monitor;
import glib.agent.event.Event;
import glib.agent.event.LocalDCStatusUpdateEvent;

public class MonitorThread implements Runnable {
	
	private Monitor monitor;
	private Logger log = LogManager.getLogger(MonitorThread.class);
	public MonitorThread(Monitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				Measure measure = monitor.monitor();
				DCManager dcManager = DCManager.getDCManager();
				LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
				localDCStatus.update(measure.getCapacity(), measure.getLoad(), null);
				LocalDCStatusUpdateEvent event = new LocalDCStatusUpdateEvent();
				Queue<Event> eventQueue = EventQueue.getEventQueue();
				eventQueue.add(event);
				eventQueue.notifyAll();
				Thread.sleep(2000);
			} catch (Exception e) {
				log.catching(e);
			}
		}
	}

}
