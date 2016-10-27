package glb.agent.handler;

import java.util.Map;

public abstract class RedistributionEventHandlerLoader {

	public abstract RedistributionEventHandler load(Map<?, ?> parameters) throws Exception;
}
