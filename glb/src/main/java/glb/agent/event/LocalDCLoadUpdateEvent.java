package glb.agent.event;


public class LocalDCLoadUpdateEvent extends DCStatusUpdateEvent {
	
	private int load;
	
	public LocalDCLoadUpdateEvent(int load) {
		super(EventType.LocalDCLoadUpdateEvent);
		this.load = load;
	}

	public int getLoad() {
		return load;
	}
}
