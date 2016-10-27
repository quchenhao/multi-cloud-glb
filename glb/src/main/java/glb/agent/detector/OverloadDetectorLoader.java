package glb.agent.detector;

import java.util.Map;

public abstract class OverloadDetectorLoader {
	public abstract OverloadDetector load(Map<?, ?> parameters) throws Exception;
}
