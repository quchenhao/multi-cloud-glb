package glb.agent.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.event.Event;

public abstract class EventHandler {
	
	protected Logger log = LogManager.getLogger(EventHandler.class);

	public abstract void handle(Event event);
}
