package glb.agent.handler;

import java.util.Collection;
import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.Server;
import glb.agent.core.dc.ServerStatus;
import glb.agent.event.Event;
import glb.agent.event.LocalDCCapacityUpdateEvent;
import glb.agent.event.LocalDCStatusUpdateEvent;

public class LocalDCCapacityUpdateEventHandler extends EventHandler{

	@Override
	public Feedback handle(Event event) {
		LocalDCCapacityUpdateEvent localDCCapacityUpdateEvent = (LocalDCCapacityUpdateEvent)event;
		
		Collection<Server> servers = localDCCapacityUpdateEvent.getServers();
		
		boolean isChange = false;
		
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		
		for (Server temp : servers) {
			String serverId = temp.getServerId();
			Server server = localDCStatus.getServer(serverId);
			if (server == null && temp.getServerStatus() == ServerStatus.RUNNING) {
				isChange = true;
				localDCStatus.addOrReplaceServer(server);
			}
			else if (server != null && temp.getServerStatus() != ServerStatus.RUNNING) {
				isChange = true;
				localDCStatus.removeServer(serverId);
			}
			else if (server != null && temp.getServerStatus() == ServerStatus.RUNNING) {
				if (server.isHealthy() != temp.isHealthy()) {
					isChange = true;
					localDCStatus.addOrReplaceServer(temp);
				}
			}
		}
		
		if (isChange) {
			LocalDCStatusUpdateEvent localDCStatusUpdateEvent = new LocalDCStatusUpdateEvent();
			Queue<Event> eventQueue = EventQueue.getEventQueue();
			synchronized(eventQueue) {
				eventQueue.add(localDCStatusUpdateEvent);
				eventQueue.notify();
			}
			
			return new Feedback(LocalDCStatusChangeLevel.NOTABLE_CHANGE);
		}
		
		return new Feedback(LocalDCStatusChangeLevel.NO_CHANGE);
	}

}
