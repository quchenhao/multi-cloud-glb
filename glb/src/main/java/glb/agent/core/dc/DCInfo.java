package glb.agent.core.dc;

public class DCInfo {

	private String dcId;
	
	private String address;
	
	private int port;
	
	DCInfo(String dcId, String address, int port) {
		this.dcId = dcId;
		this.address = address;
		this.port = port;
	}

	public String getDcId() {
		return dcId;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	
}
