package glb.agent.core;

import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.comm.PublishReminder;
import glb.agent.event.Event;
import glb.agent.event.EventType;
import glb.agent.handler.EventHandler;
import glb.agent.handler.Feedback;
import glb.agent.handler.LocalDCStatusChangeLevel;

public class EventHandlingThread implements Runnable {
	
	private Map<EventType, EventHandler> handlers;
	private PublishReminder publishReminder;
	private Logger log = LogManager.getLogger(EventHandlingThread.class);
	
	public EventHandlingThread(Map<EventType, EventHandler> handlers, PublishReminder publishReminder) {
		this.handlers = handlers;
		this.publishReminder = publishReminder;
	}

	@Override
	public void run() {
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		while (true) {
			if (!eventQueue.isEmpty()) {
				Event event = null;
				synchronized(eventQueue) {
					event = eventQueue.poll();
				}
				
				EventHandler eventHandler = handlers.get(event.getEventType());
				if (eventHandler == null) {
					log.error("cannot find handler for event type " + event.getEventType());
					continue;
				}
				else {
					log.trace("start handling event " + event.getEventType());
					Feedback feedback = eventHandler.handle(event);
					LocalDCStatusChangeLevel changeLevel = feedback.getLocalDCStatusChangeLevel();
					if (changeLevel == LocalDCStatusChangeLevel.NOTABLE_CHANGE) {
						synchronized(publishReminder) {
							publishReminder.notify();
						}
					}
					log.trace("finish handling event " + event.getEventType() + " " + changeLevel);
				}
			}
			else {
				try {
					synchronized(eventQueue){
						eventQueue.wait();
					}
				} catch (InterruptedException e) {
					log.catching(e);
				}
			}
		}
		
	}

}
