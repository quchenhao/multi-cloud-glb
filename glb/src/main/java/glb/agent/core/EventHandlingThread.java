package glb.agent.core;

import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.handler.EventHandler;
import glib.agent.event.Event;

public class EventHandlingThread implements Runnable {
	
	private Map<String, EventHandler> handlers;
	private Logger log = LogManager.getLogger(EventHandlingThread.class);
	
	public EventHandlingThread(Map<String, EventHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void run() {
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		
		while (true) {
			if (!eventQueue.isEmpty()) {
				Event event = eventQueue.poll();
				EventHandler eventHandler = handlers.get(event.getName());
				if (eventHandler == null) {
					log.error("cannot find handler for event type " + event.getName());
					continue;
				}
				else {
					log.trace("start handling event " + event.getName());
					eventHandler.handle(event);
					log.trace("finish handling event " + event.getName());
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
