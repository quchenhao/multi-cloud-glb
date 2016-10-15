package glb.agent.decision.latency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;
import com.joptimizer.optimizers.PrimalDualMethod;

import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.RemoteDCStatus;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.decision.LoadDistributionPlanGenerator;

public class LatencyAwareLoadDistributionPlanGenerator extends LoadDistributionPlanGenerator{

	@Override
	public LoadDistributionPlan generateOverloadHandlingPlan(LocalDCStatus localDCStatus,
			Collection<RemoteDCStatus> remoteDCStatuses) throws Exception {
		
		List<RemoteDCStatus> remoteDCStatusesList = new ArrayList<RemoteDCStatus>(remoteDCStatuses);
		
		int size = remoteDCStatuses.size();
		double[] maxServiceRates = new double[size];
		double[] capacities = new double[size];
		double[] availableCapacities = new double[size];
		double[] diffs = new double[size];
		double[] latencies = new double[size];
		
		double small = 0.001;
		
		Map<String, Integer> outSourcedLoad = localDCStatus.getOutsourcedLoad();
		
		int totalAvailableCapacity = 0;
		
		for (int i = 0; i < size; i++) {
			RemoteDCStatus dcStatus = remoteDCStatusesList.get(i);
			capacities[i] = dcStatus.getCapacity();
			maxServiceRates[i] = dcStatus.getMaxServiceRate();
			double load = dcStatus.getTotalLoad();
			
			if (outSourcedLoad.containsKey(dcStatus.getDCId())) {
				load = load - outSourcedLoad.get(dcStatus.getDCId());
			}
			
			if (load > maxServiceRates[i]) {
				diffs[i] = small;
			}
			else {
				diffs[i] = maxServiceRates[i] - load;
			}
			
			if (capacities[i] < load) {
				availableCapacities[i] = capacities[i] - load;
				totalAvailableCapacity += availableCapacities[i];
			}
			else {
				availableCapacities[i] = small;
			}
			
			latencies[i] = dcStatus.getLatency();
		}
		
		int excess = localDCStatus.getCapacity() - localDCStatus.getMostRecentLoad();
		int loadToReject = 0;
		int totalOutSource = excess;
		
		if (excess > totalAvailableCapacity) {
			loadToReject = excess - totalAvailableCapacity;
			totalOutSource = totalAvailableCapacity;
		}
		
		LatencyOptimizationFunction latencyOptimizationFunction = new LatencyOptimizationFunction(capacities, diffs, latencies);
		List<ConvexMultivariateRealFunction> inequalities = new ArrayList<ConvexMultivariateRealFunction>();
		
		double[] allZeros = new double[size];
		
		for (int i = 0; i < size; i++) {
			double[] zeroConstraint = allZeros.clone();
			zeroConstraint[i] = -1;
			ConvexMultivariateRealFunction funtion1 = new LinearMultivariateRealFunction(zeroConstraint, 0);
			inequalities.add(funtion1);
			
			if (availableCapacities[i] < totalOutSource) {
				double[] capacityConstraint = allZeros.clone();
				capacityConstraint[i] = 1;
				ConvexMultivariateRealFunction funtion2 = new LinearMultivariateRealFunction(capacityConstraint, availableCapacities[i]);
				inequalities.add(funtion2);
			}
		}
		
		double[][] equality = new double[1][size];
		
		for (int i = 0; i < size; i++) {
			equality[0][i] = 1;
		}
		
		OptimizationRequest optimizationRequest = new OptimizationRequest();
		optimizationRequest.setF0(latencyOptimizationFunction);
		optimizationRequest.setInitialPoint(new double[] { 0.9, 0.1 });
		optimizationRequest.setFi((ConvexMultivariateRealFunction[])inequalities.toArray());
		
		optimizationRequest.setA(equality);
		optimizationRequest.setB(new double[] { totalOutSource });
		optimizationRequest.setTolerance(1.E-9);
		
		PrimalDualMethod optimization = new PrimalDualMethod();
		optimization.setOptimizationRequest(optimizationRequest);
		int returnCode = optimization.optimize();
		
		OptimizationResponse optimizationResponse = optimization.getOptimizationResponse();
		
		double[] answer = optimizationResponse.getSolution();
		
		Map<String, Integer> outSourcePlan = new HashMap<String, Integer>();
		
		for (int i = 0; i < size; i++) {
			int amount = (int) Math.round(answer[i]);
			if (amount > 0) {
				RemoteDCStatus dcStatus = remoteDCStatusesList.get(i);
				outSourcePlan.put(dcStatus.getDCId(), amount);
			}
			
		}
		
		LoadDistributionPlan loadDistributionPlan = new LoadDistributionPlan(outSourcePlan, loadToReject);
		
		return loadDistributionPlan;
	}

}
