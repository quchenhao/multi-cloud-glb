package glb.agent.core.dc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DCStatus {
	
	private String dcId;
	
	protected Map<String, Integer> outSourcedLoad;
	
	public DCStatus (String dcId) {
		this.dcId = dcId;
		this.outSourcedLoad = new HashMap<String, Integer>();
	}
	
	public abstract int getCapacity();
	
	public abstract int getTotalLoad();
	
	public String getDCId() {
		return dcId;
	}
	
	public abstract void updateLoad(int load);
	
	public synchronized void updateOutSourcedLoad(Map<String, Integer> outSourcedLoad) {
		if (outSourcedLoad != null) {
			this.outSourcedLoad.clear();
			if (!outSourcedLoad.isEmpty()) {
				this.outSourcedLoad.putAll(outSourcedLoad);
			}
		}
	}
	
	public synchronized Map<String, Integer> getOutsourcedLoad() {
		return Collections.unmodifiableMap(outSourcedLoad);
	}
	
	public abstract int getMaxServiceRate();
}
