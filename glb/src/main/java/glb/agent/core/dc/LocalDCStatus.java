package glb.agent.core.dc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDCStatus extends DCStatus {
	
	private List<Integer> recentLoads;
	
	private int windowSize;
	
	private Map<String, Server> servers;
	
	public LocalDCStatus (String dcId, int windowSize) {
		super(dcId);
		this.windowSize = windowSize;
		this.servers = new HashMap<String, Server>();
	}
	
	@Override
	public synchronized void updateLoad(int load) {
		if (recentLoads.size() == windowSize) {
			recentLoads.remove(0);
		}
		
		recentLoads.add(load);
	}
	
	@Override
	public synchronized int getTotalLoad() {
		return getMostRecentLoad();
	}

	public synchronized int getMeanLoad() {
		int sum = 0;
		for (Integer value : recentLoads) {
			sum += value;
		}
		
		return (int)Math.ceil((sum + 0.0)/recentLoads.size());
	}

	public synchronized int getMostRecentLoad() {
		return recentLoads.get(recentLoads.size() - 1);
	}
	
	public synchronized Server getServer(String serverId) {
		return servers.get(serverId);
	}
	
	public synchronized void addOrReplaceServer(Server server) {
		servers.put(server.getServerId(), server);
	}
	
	public synchronized void removeServer(String serverId) {
		servers.remove(serverId);
	}
	
	public synchronized Collection<Server> getAllHealthyServers() {
		
		List<Server> healthyServers = new ArrayList<Server>();
		for (Server server : servers.values()) {
			if (server.getServerStatus() == ServerStatus.RUNNING && server.isHealthy()) {
				healthyServers.add(server);
			}
		}
		
		return Collections.unmodifiableCollection(healthyServers);
	}
	
	public List<Integer> getRecentLoads() {
		return Collections.unmodifiableList(recentLoads);
	}
	
	@Override
	public synchronized int getCapacity() {
		
		Collection<Server> healthyServers = getAllHealthyServers();
		int capacity = 0;
		
		for (Server server : healthyServers) {
			capacity += server.getCapacity();
		}
		return capacity;
	}
}
