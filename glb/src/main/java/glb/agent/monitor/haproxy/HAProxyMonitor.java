package glb.agent.monitor.haproxy;


import org.apache.http.client.methods.HttpGet;

import glb.agent.monitor.Measure;
import glb.agent.monitor.Monitor;


public class HAProxyMonitor extends Monitor {

private String address;
	
	private int port;
	
	private String url;
	
	private HttpGet request;
	
	public HAProxyMonitor(String address, int port, String url) {
		this.address = address;
		this.port = port;
		this.url = url;
	}
	
	@Override
	public Measure monitor() {
		
		return null;
	}

}
