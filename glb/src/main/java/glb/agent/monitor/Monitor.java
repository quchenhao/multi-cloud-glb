package glb.agent.monitor;

public abstract class Monitor {

	public abstract Measure monitor() throws Exception;
	public abstract void calibrate(int adjust);
}
