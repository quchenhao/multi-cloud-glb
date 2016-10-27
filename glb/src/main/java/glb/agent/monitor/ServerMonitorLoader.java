package glb.agent.monitor;

import java.util.Map;

import glb.agent.core.dc.ServerType;

public abstract class ServerMonitorLoader {
	public abstract ServerMonitor load(Map<?, ?> parameters, Map<String, ServerType> serverTypes) throws Exception;
}
