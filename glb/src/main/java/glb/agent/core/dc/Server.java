package glb.agent.core.dc;

public class Server {
	
	private String serverId;
	private int maxServiceRate;
	private int capacity;
	private String address;
	private int port;
	private boolean isHealthy;
	private ServerStatus serverStatus;
	
	public Server(String serverId, int maxServiceRate, int capacity, String address, int port) {
		this.serverId = serverId;
		this.maxServiceRate = maxServiceRate;
		this.capacity = capacity;
		this.address = address;
		this.port = port;
		this.serverStatus = ServerStatus.PENDING;
	}

	public String getServerId() {
		return serverId;
	}
	
	public int getMaxServiceRate() {
		return maxServiceRate;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public synchronized boolean isHealthy() {
		return isHealthy;
	}
	
	public synchronized void setIsRunning(boolean isHealthy) {
		this.isHealthy = isHealthy;
	}
	
	public synchronized ServerStatus getServerStatus() {
		return serverStatus;
	}
	
	public synchronized void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}
	
	public synchronized void setIsHealthey(boolean isHealthy) {
		this.isHealthy = isHealthy;
	}
}
