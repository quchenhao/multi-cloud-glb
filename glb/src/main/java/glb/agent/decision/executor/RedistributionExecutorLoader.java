package glb.agent.decision.executor;

import java.util.Map;

public abstract class RedistributionExecutorLoader {

	public abstract RedistributionExecutor load(Map<?, ?> parameters) throws Exception;
}
