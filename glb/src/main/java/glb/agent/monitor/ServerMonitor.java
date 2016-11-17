package glb.agent.monitor;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.Server;
import glb.agent.core.dc.ServerType;
import glb.agent.event.Event;
import glb.agent.event.LocalDCCapacityUpdateEvent;

public abstract class ServerMonitor extends Monitor {

	protected String tagHead;
	protected Map<String, ServerType> serverTypes;
	protected int port;

	public ServerMonitor(String tagName, Map<String, ServerType> serverTypes, int port) {
		this.tagHead = tagName;
		this.serverTypes = serverTypes;
		this.port = port;
	}

	@Override
	public void monitor() {

		Collection<Server> servers = getServers();

		LocalDCCapacityUpdateEvent localDCCapacityUpdateEvent = new LocalDCCapacityUpdateEvent(servers);
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		synchronized (eventQueue) {
			eventQueue.add(localDCCapacityUpdateEvent);
			eventQueue.notify();
		}

	}

	protected abstract Collection<Server> getServers();
}
