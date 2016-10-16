package glb.agent.monitor;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.Server;
import glb.agent.core.dc.ServerStatus;
import glb.agent.event.Event;
import glb.agent.event.LocalDCStatusUpdateEvent;

public abstract class ServerMonitor extends Monitor{

	protected String tagHead;
	protected Map<String, Integer> capacityMap;
	protected Map<String, Integer> maxServiceRateMap;
	protected int port;
	
	public ServerMonitor(String tagName, Map<String, Integer> maxServiceRateMap, Map<String, Integer> capacityMap, int port) {
		this.tagHead = tagName;
		this.maxServiceRateMap = maxServiceRateMap;
		this.capacityMap = capacityMap;
		this.port = port;
	}
	
	@Override
	public void monitor() {
		boolean isChange = false;
		
		Collection<Server> servers = getServers();
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
			eventQueue.add(localDCStatusUpdateEvent);
			eventQueue.notifyAll();
		}
	}
	
	protected abstract Collection<Server> getServers();
}
