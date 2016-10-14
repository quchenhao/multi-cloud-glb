package glb.agent.decision;

import java.util.Map;
import java.util.Map.Entry;

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
	
	@Override
	public String toString() {
		String str = "load to reject: " + loadToReject + "; load to outsource:";
		
		for (Entry<String, Integer> entry : outSourcePlan.entrySet()) {
			str += " (" + entry.getKey() + ", " + entry.getValue() + ")";
		}
		
		return str;
	}
}
