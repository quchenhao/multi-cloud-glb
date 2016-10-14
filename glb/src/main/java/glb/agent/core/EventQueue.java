package glb.agent.core;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import glb.agent.event.Event;

public class EventQueue {

	private static Queue<Event> queue = new SynchronousQueue<Event>();
	
	public static Queue<Event> getEventQueue() {
		return queue;
	}
}
