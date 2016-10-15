package glb.agent.decision.latency;

import com.joptimizer.functions.StrictlyConvexMultivariateRealFunction;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

public class LatencyOptimizationFunction implements StrictlyConvexMultivariateRealFunction {
	
	private DoubleFactory1D f1;
	private DoubleFactory2D f2;
	private DoubleMatrix1D capacities;
	private DoubleMatrix1D loads;
	private DoubleMatrix1D latencies;
	private DoubleMatrix1D diffs;
	private DoubleMatrix1D doubledcapacities;
	private int dim;
	
	public LatencyOptimizationFunction(double[] capacities, double[] loads, double[] latencies) {
		if (capacities.length != loads.length && loads.length != latencies.length) {
			throw new IllegalArgumentException("capacities, loads, and latencies should have the same size.");
		}
		this.f1 = DoubleFactory1D.dense;
		this.f2 = DoubleFactory2D.sparse;
		this.capacities = f1.make(capacities);
		this.loads = f1.make(loads);
		this.latencies = f1.make(latencies);
		this.diffs = this.capacities.assign(this.loads, Functions.minus);
		this.doubledcapacities = this.capacities.assign(Functions.mult(2));
		this.dim = capacities.length;
	}

	@Override
	public int getDim() {
		return dim;
	}

	@Override
	public double[] gradient(double[] x) {
		DoubleMatrix1D xMatrix = f1.make(x);
		DoubleMatrix1D tempMatrix = diffs.assign(xMatrix, Functions.minus);
		tempMatrix = tempMatrix.assign(Functions.square);
		tempMatrix = capacities.assign(tempMatrix, Functions.div);
		
		return tempMatrix.assign(latencies, Functions.plus).toArray();
	}

	@Override
	public double[][] hessian(double[] x) {
		DoubleMatrix1D xMatrix = f1.make(x);
		DoubleMatrix1D tempMatrix = diffs.assign(xMatrix, Functions.minus);
		tempMatrix = tempMatrix.assign(Functions.pow(3));
		tempMatrix = doubledcapacities.assign(tempMatrix, Functions.div);
		
		DoubleMatrix2D hessian = f2.diagonal(tempMatrix);
		return hessian.toArray();
	}

	@Override
	public double value(double[] x) {
		DoubleMatrix1D xMatrix = f1.make(x);
		double temp = xMatrix.zDotProduct(latencies);
		DoubleMatrix1D tempMatrix = diffs.assign(xMatrix, Functions.minus);
		double temp2 = capacities.aggregate(tempMatrix, Functions.plus, Functions.div);
		return temp + temp2;
	}

}
