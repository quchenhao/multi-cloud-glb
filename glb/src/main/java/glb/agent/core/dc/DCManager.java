package glb.agent.core.dc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCManager {

	private static DCManager dcManager;
	
	private Map<String, DCStatus> remoteStatuses;
	
	private Map<String, DCInfo> infos;
	
	private DCStatus localDCStatus;
	
	private String localDCId;
	
	private DCManager (Map<String, DCInfo> infos, String localDCId) {
		this.infos = infos;
		this.localDCId = localDCId;
		initializeDCStatuses();
	}

	private void initializeDCStatuses() {
		remoteStatuses = new HashMap<String, DCStatus>();
		
		for (DCInfo dcInfo : infos.values()) {
			String dcId = dcInfo.getDcId();
			DCStatus dcStatus = new DCStatus(dcId);
			remoteStatuses.put(dcId, dcStatus);
		}
		
		if (remoteStatuses.containsKey(localDCId)) {
			localDCStatus = remoteStatuses.remove(localDCId);
		}
		else {
			throw new IllegalArgumentException("unknown local dc id:" + localDCId);
		}
	}
	
	public DCStatus getLocalDCStatus() {
		return localDCStatus;
	}
	
	public Collection<DCStatus> getRemoteDCStatuses() {
		return Collections.unmodifiableCollection(remoteStatuses.values());
	}
	
	public void updateLocalDCStatus(int capacity, int load) {
		localDCStatus.update(capacity, load);
	}
	
	public void updateRemoteDCStatus(String dcId, int capacity, int load) {
		DCStatus dcStatus = remoteStatuses.get(dcId);
		if (dcStatus == null) {
			throw new NullPointerException("unknown " + dcId);
		}
		
		dcStatus.update(capacity, load);
	}
	
	public String getLocalDCId() {
		return localDCId;
	}
	
	static void initializeDCManager(Map<String, DCInfo> infos, String localDCId) {
		dcManager = new DCManager(infos, localDCId);
	}
	
	public static DCManager getDCManager() {
		return dcManager;
	}
}
