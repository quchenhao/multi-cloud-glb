package glb.agent.monitor;

public class Measure {

	private int capacity;
	
	private int load;
	
	public Measure(int capacity, int load) {
		this.capacity = capacity;
		this.load = load;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getLoad() {
		return load;
	}
}
