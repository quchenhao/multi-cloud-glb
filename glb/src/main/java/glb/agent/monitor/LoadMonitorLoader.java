package glb.agent.monitor;

import java.util.Map;

public abstract class LoadMonitorLoader {
	
	public abstract LoadMonitor load(Map<?, ?> parameters) throws Exception;
}
