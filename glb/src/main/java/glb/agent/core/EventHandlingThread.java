package glb.agent.core;

import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.event.Event;
import glb.agent.event.EventType;
import glb.agent.handler.EventHandler;

public class EventHandlingThread implements Runnable {
	
	private Map<EventType, EventHandler> handlers;
	private Logger log = LogManager.getLogger(EventHandlingThread.class);
	
	public EventHandlingThread(Map<EventType, EventHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void run() {
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		while (true) {
			if (!eventQueue.isEmpty()) {
				Event event = eventQueue.poll();
				EventHandler eventHandler = handlers.get(event.getEventType());
				if (eventHandler == null) {
					log.error("cannot find handler for event type " + event.getEventType());
					continue;
				}
				else {
					log.trace("start handling event " + event.getEventType());
					eventHandler.handle(event);
					log.trace("finish handling event " + event.getEventType());
				}
			}
			else {
				try {
					eventQueue.wait();
				} catch (InterruptedException e) {
					log.catching(e);
				}
			}
		}
		
	}

}
