package glb.agent.decision.latency;

import java.util.ArrayList;
import java.util.Arrays;
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

	private double small = 0.1;
	
	LatencyAwareLoadDistributionPlanGenerator() {}
	
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
		
		Map<String, Integer> outSourcedLoad = localDCStatus.getOutsourcedLoad();
		
		int totalAvailableCapacity = 0;
		
		for (int i = 0; i < size; i++) {
			RemoteDCStatus dcStatus = remoteDCStatusesList.get(i);
			capacities[i] = dcStatus.getCapacity();
			System.out.print(capacities[i] + " ");
			maxServiceRates[i] = dcStatus.getMaxServiceRate();
			System.out.print(maxServiceRates[i] + " ");
			double load = dcStatus.getTotalLoad();
			System.out.print(load + " ");
			
			if (outSourcedLoad.containsKey(dcStatus.getDCId())) {
				load = load - outSourcedLoad.get(dcStatus.getDCId());
			}
			
			if (maxServiceRates[i] > load) {
				diffs[i] = maxServiceRates[i] - load + 2 * small;
			}
			else {
				diffs[i] = 2 * small;
			}
			
			System.out.print(diffs[i] + " ");
			
			if (capacities[i] > load) {
				availableCapacities[i] = capacities[i] - load + 2 * small;
				totalAvailableCapacity += availableCapacities[i];
			}
			else {
				availableCapacities[i] = 2 * small;
			}
			
			System.out.print(availableCapacities[i] + " ");
			
			latencies[i] = dcStatus.getLatency()/1000.0;
			
			System.out.println(latencies[i]);
		}
		
		int excess =  localDCStatus.getMostRecentLoad() - localDCStatus.getCapacity();
		int loadToReject = 0;
		double totalOutSource = excess + small * size;
		
		if (excess > totalAvailableCapacity - 2 * small * size) {
			loadToReject = (int)(excess - totalAvailableCapacity + 2 * small * size);
			totalOutSource = totalAvailableCapacity - small * size;
		}
		
		System.out.println(loadToReject);
		System.out.println(totalOutSource);
		
		LatencyOptimizationFunction latencyOptimizationFunction = new LatencyOptimizationFunction(maxServiceRates, diffs, latencies);
		List<ConvexMultivariateRealFunction> inequalities = new ArrayList<ConvexMultivariateRealFunction>();
		
		for (int i = 0; i < size; i++) {
			double[] zeroConstraint = new double[size];
			zeroConstraint[i] = -1;
			ConvexMultivariateRealFunction funtion1 = new LinearMultivariateRealFunction(zeroConstraint, 0);
			inequalities.add(funtion1);
			
			if (availableCapacities[i] < totalOutSource) {
				double[] capacityConstraint = new double[size];
				capacityConstraint[i] = 1;
				ConvexMultivariateRealFunction funtion2 = new LinearMultivariateRealFunction(capacityConstraint, -availableCapacities[i]);
				inequalities.add(funtion2);
			}
		}
		
		double[][] equality = new double[1][size];
		
		for (int i = 0; i < size; i++) {
			equality[0][i] = 1;
		}
		
		double[] initialSolution = generateInitialSolution(availableCapacities, totalOutSource);
		
		for (int k = 0; k < initialSolution.length; k++) {
			System.out.print(initialSolution[k] + " ");
		}
		
		OptimizationRequest optimizationRequest = new OptimizationRequest();
		optimizationRequest.setF0(latencyOptimizationFunction);
		optimizationRequest.setInitialPoint(initialSolution);
		
		Object[] objects = inequalities.toArray();
		ConvexMultivariateRealFunction[] inequalitiesConstraints = Arrays.copyOf(objects, objects.length, ConvexMultivariateRealFunction[].class);
		
		optimizationRequest.setFi(inequalitiesConstraints);
		
		optimizationRequest.setA(equality);
		optimizationRequest.setB(new double[] { totalOutSource });
		optimizationRequest.setTolerance(0.1);
		
		PrimalDualMethod optimization = new PrimalDualMethod();
		optimization.setOptimizationRequest(optimizationRequest);
		optimization.optimize();
		
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

	private double[] generateInitialSolution(double[] availableCapacities, double totalOutSource) {
		double[] solution = new double [availableCapacities.length];
		
		double remain = totalOutSource;
		for (int i = 0; i < availableCapacities.length; i++) {
			solution[i] = small;
			remain -= small;
		}
		
		for (int i = 0; i < availableCapacities.length; i++) {
			if (remain != 0) {
				if (availableCapacities[i] - 2 * small <= remain) {
					solution[i] += (availableCapacities[i] - 2 * small);
					remain -= (availableCapacities[i] - 2 * small);
				}
				else {
					solution[i] += remain;
					remain = 0;
				}
			}
			else {
				break;
			}
		}
		
		
		
		return solution;
	}

}
