package glb.agent.event;

import java.util.Collection;

import glb.agent.core.dc.Server;

public class LocalDCCapacityUpdateEvent extends Event{
	
	private Collection<Server> servers;

	public LocalDCCapacityUpdateEvent(Collection<Server> servers) {
		super(EventType.LocalDCCapacityUpdateEvent);
		this.servers = servers;
	}
	
	public Collection<Server> getServers() {
		return servers;
	}

}
