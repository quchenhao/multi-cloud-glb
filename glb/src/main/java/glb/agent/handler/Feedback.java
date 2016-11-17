package glb.agent.handler;

public class Feedback {

	private LocalDCStatusChangeLevel changeLevel;
	
	Feedback(LocalDCStatusChangeLevel changeLevel) {
		this.changeLevel = changeLevel;
	}
	
	public LocalDCStatusChangeLevel getLocalDCStatusChangeLevel() {
		return changeLevel;
	}
}
