package glb.agent.detector.possion;

import java.util.Map;

import glb.agent.detector.OverloadDetector;
import glb.agent.detector.OverloadDetectorLoader;

public class PossionOverloadDetectorLoader extends OverloadDetectorLoader {

	@Override
	public OverloadDetector load(Map<?, ?> parameters) throws Exception {
		
		return new PossionOverloadDetector();
	}

}
