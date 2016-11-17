package haproxy16.glb.agent.decision.executor;

import java.util.Map;

import glb.agent.decision.executor.RedistributionExecutorLoader;
import haproxy16.glb.agent.decision.executor.weight.HAProxy16WeightCalculator;
import glb.agent.decision.executor.RedistributionExecutor;

public class SimpleHAProxy16RedistributionExecutorLoader extends RedistributionExecutorLoader {

	@Override
	public RedistributionExecutor load(Map<?, ?> parameters) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)parameters;
		String weightCalculatorClass = (String)map.get("weight_calculator");
		String baseFile = (String)map.get("base_file_path");
		HAProxy16WeightCalculator weightCalculator = (HAProxy16WeightCalculator) Class.forName(weightCalculatorClass).newInstance();
		return new SimpleHAProxy16RedistributionExecutor(weightCalculator, baseFile);
	}

}
