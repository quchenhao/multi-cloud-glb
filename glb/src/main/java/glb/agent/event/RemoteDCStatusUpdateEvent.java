package glb.agent.event;

public class RemoteDCStatusUpdateEvent extends DCStatusUpdateEvent{

	private String dcId;
	
	public RemoteDCStatusUpdateEvent(String dcId) {
		super(EventType.RemoteDCStatusUpdateEvent);
		this.dcId = dcId;
	}

	public String getDCId() {
		return dcId;
	}
}
