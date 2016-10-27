package haproxy16.glb.agent.handler;

import java.util.Map;

import glb.agent.handler.RedistributionEventHandler;
import glb.agent.handler.RedistributionEventHandlerLoader;
import glb.agent.handler.weight.WeightCalculator;

public class SimpleHAProxy16RedistributionEventHandlerLoader extends RedistributionEventHandlerLoader {

	@Override
	public RedistributionEventHandler load(Map<?, ?> parameters) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)parameters;
		String weightCalculatorClass = (String)map.get("weight_calculator");
		String baseFile = (String)map.get("base_file_path");
		WeightCalculator weightCalculator = (WeightCalculator) Class.forName(weightCalculatorClass).newInstance();
		return new SimpleHAProxy16RedistributionEventHandler(weightCalculator, baseFile);
	}

}
