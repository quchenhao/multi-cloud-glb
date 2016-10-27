package glb.agent.core.dc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCManager {

	private static DCManager dcManager;
	
	private Map<String, RemoteDCStatus> remoteStatuses;
	
	private Map<String, DCInfo> infos;
	
	private LocalDCStatus localDCStatus;
	
	private String localDCId;
	
	private int windowSize;
	
	private DCManager (Map<String, DCInfo> infos, Map<String, Integer> latencies, String localDCId, int windowSize) {
		this.infos = infos;
		this.localDCId = localDCId;
		this.windowSize = windowSize;
		initializeDCStatuses(latencies);
	}

	private void initializeDCStatuses(Map<String, Integer> latencies) {
		remoteStatuses = new HashMap<String, RemoteDCStatus>();
		
		for (DCInfo dcInfo : infos.values()) {
			String dcId = dcInfo.getDcId();
			if (dcId.equals(localDCId)) {
				continue;
			}
			RemoteDCStatus dcStatus = new RemoteDCStatus(dcId, latencies.get(dcId));
			remoteStatuses.put(dcId, dcStatus);
		}
		
		localDCStatus = new LocalDCStatus(localDCId, windowSize);
	}
	
	public LocalDCStatus getLocalDCStatus() {
		return localDCStatus;
	}
	
	public Collection<RemoteDCStatus> getRemoteDCStatuses() {
		return Collections.unmodifiableCollection(remoteStatuses.values());
	}
	
	public String getLocalDCId() {
		return localDCId;
	}
	
	public RemoteDCStatus getRemoteDCStatus(String dcId) {
		return remoteStatuses.get(dcId);
	}
	
	public boolean containsDC(String dcId) {
		return remoteStatuses.containsKey(dcId);
	}
	
	public DCInfo getDCInfo(String dcId) {
		return infos.get(dcId);
	}
	
	public static void initializeDCManager(Map<String, DCInfo> infos, Map<String, Integer> latencies, String localDCId, int windowSize) {
		dcManager = new DCManager(infos, latencies, localDCId, windowSize);
	}
	
	public static DCManager getDCManager() {
		return dcManager;
	}
}
