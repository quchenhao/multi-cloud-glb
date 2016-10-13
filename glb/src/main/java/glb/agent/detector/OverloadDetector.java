package glb.agent.detector;

import glb.agent.core.dc.LocalDCStatus;

public abstract class OverloadDetector {
	public abstract boolean isOverload(LocalDCStatus loacalDCStatus);
}
