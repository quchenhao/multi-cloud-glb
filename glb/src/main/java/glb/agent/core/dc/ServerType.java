package glb.agent.core.dc;

public class ServerType {

	private String type;
	private int serviceRate;
	private int Capacity;
	
	public ServerType(String type, int serviceRate, int Capacity) {
		this.type = type;
		this.serviceRate = serviceRate;
		this.Capacity = Capacity;
	}

	public String getType() {
		return type;
	}

	public int getServiceRate() {
		return serviceRate;
	}

	public int getCapacity() {
		return Capacity;
	}
	
	
}
