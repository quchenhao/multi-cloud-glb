package haproxy16.glb.agent.monitor;

import java.util.Map;
import glb.agent.monitor.LoadMonitor;
import glb.agent.monitor.LoadMonitorLoader;

public class SimpleHAProxy16LoadMonitorLoader extends LoadMonitorLoader{

	@Override
	public LoadMonitor load(Map<?, ?> parameters) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)parameters;
		String address = (String)map.get("adress");
		int port = (Integer)map.get("port");
		String url = (String)map.get("url");
		String username = (String)map.get("username");
		String password = (String)map.get("password");
		return new SimpleHAProxy16LoadMonitor(address, port, url, username, password);
	}

}
