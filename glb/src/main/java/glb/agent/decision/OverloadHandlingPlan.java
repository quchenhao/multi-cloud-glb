package glb.agent.decision;

import java.util.Map;

public class OverloadHandlingPlan {

	private Map<String, Integer> outSourcePlan;
	
	private int loadToReject;
	
	public OverloadHandlingPlan(Map<String, Integer> outSourcePlan, int loadToReject) {
		this.outSourcePlan = outSourcePlan;
		this.loadToReject = loadToReject;
	}

	public Map<String, Integer> getOutSourcePlan() {
		return outSourcePlan;
	}

	public int getLoadToReject() {
		return loadToReject;
	}
	
	
}
