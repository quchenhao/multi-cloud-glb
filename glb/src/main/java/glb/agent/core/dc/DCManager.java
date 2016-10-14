package glb.agent.core.dc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCManager {

	private static DCManager dcManager;
	
	private Map<String, DCStatus> remoteStatuses;
	
	private Map<String, DCInfo> infos;
	
	private LocalDCStatus localDCStatus;
	
	private String localDCId;
;
	
	private int windowSize;
	
	private DCManager (Map<String, DCInfo> infos, String localDCId, int windowSize) {
		this.infos = infos;
		this.localDCId = localDCId;
		this.windowSize = windowSize;
		initializeDCStatuses();
	}

	private void initializeDCStatuses() {
		remoteStatuses = new HashMap<String, DCStatus>();
		
		for (DCInfo dcInfo : infos.values()) {
			String dcId = dcInfo.getDcId();
			if (dcId.equals(localDCId)) {
				continue;
			}
			DCStatus dcStatus = new DCStatus(dcId);
			remoteStatuses.put(dcId, dcStatus);
		}
		
		localDCStatus = new LocalDCStatus(localDCId, windowSize);
	}
	
	public LocalDCStatus getLocalDCStatus() {
		return localDCStatus;
	}
	
	public Collection<DCStatus> getRemoteDCStatuses() {
		return Collections.unmodifiableCollection(remoteStatuses.values());
	}
	
	public void updateLocalDCStatus(int capacity, int load, Map<String, Integer> outSourcedLoad) {
		localDCStatus.update(capacity, load, outSourcedLoad);
	}
	
	public void updateRemoteDCStatus(String dcId, int capacity, int totalLoad, Map<String, Integer> outSourcedLoad) {
		DCStatus dcStatus = remoteStatuses.get(dcId);
		if (dcStatus == null) {
			throw new NullPointerException("unknown " + dcId);
		}
		
		dcStatus.update(capacity, totalLoad, outSourcedLoad);
	}
	
	public String getLocalDCId() {
		return localDCId;
	}
	
	public DCStatus getRemoteDCStatus(String dcId) {
		return remoteStatuses.get(dcId);
	}
	
	public boolean containsDC(String dcId) {
		return remoteStatuses.containsKey(dcId);
	}
	
	static void initializeDCManager(Map<String, DCInfo> infos, String localDCId, int windowSize) {
		dcManager = new DCManager(infos, localDCId, windowSize);
	}
	
	public static DCManager getDCManager() {
		return dcManager;
	}
}
