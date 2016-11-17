package glb.agent.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.RemoteDCStatus;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.decision.executor.RedistributionExecutor;
import glb.agent.event.Event;
import glb.agent.event.RedistributionEvent;

public class RedistributionEventHandler extends EventHandler{
	
	private RedistributionExecutor redistributionExecutor;
	
	public RedistributionEventHandler(RedistributionExecutor redistributionExecutor) {
		this.redistributionExecutor = redistributionExecutor;
	}
	
	@Override
	public Feedback handle(Event event) {
		RedistributionEvent redistributionEvent = (RedistributionEvent)event;
		
		LoadDistributionPlan redistributionPlan = redistributionEvent.getRedistributionPlan();
		
		try{
			redistributionExecutor.redistribute(redistributionPlan);
		} catch (Exception e) {
			log.catching(e);
			return new Feedback(LocalDCStatusChangeLevel.NO_CHANGE);
		}
		
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		Map<String, Integer> outSourcedLoad = localDCStatus.getOutsourcedLoad();
		Map<String, Integer> outSourcePlan = redistributionPlan.getOutSourcePlan();
		
		Map<String, Integer> newOutSourcedLoad = new HashMap<String, Integer>();
		for (Entry<String, Integer> dc : outSourcePlan.entrySet()) {
			int newValue = dc.getValue();
			String dcId = dc.getKey();
			newOutSourcedLoad.put(dcId, newValue);
			RemoteDCStatus remoteDC = dcManager.getRemoteDCStatus(dcId);
			if (outSourcedLoad.containsKey(dcId)) {
				int diff = newValue - outSourcePlan.get(dcId);
				int newLoad = remoteDC.getTotalLoad() + diff;
				remoteDC.updateLoad(newLoad > 0 ? newLoad : 0);
			}
			else {
				remoteDC.updateLoad(remoteDC.getTotalLoad() + newValue);
			}
		}
		
		for (Entry<String, Integer> old : outSourcedLoad.entrySet()) {
			String dcId = old.getKey();
			if (!outSourcePlan.containsKey(dcId)) {
				RemoteDCStatus remoteDC = dcManager.getRemoteDCStatus(dcId);
				int newLoad = remoteDC.getTotalLoad() - old.getValue();
				remoteDC.updateLoad(newLoad > 0 ? newLoad : 0);
			}
		}
		
		localDCStatus.updateOutSourcedLoad(newOutSourcedLoad);
		
		if (!outSourcedLoad.isEmpty() && newOutSourcedLoad.isEmpty()) {
			return new Feedback(LocalDCStatusChangeLevel.NOTABLE_CHANGE);
		}
		else if (outSourcedLoad.isEmpty() && !newOutSourcedLoad.isEmpty()) {
			return new Feedback(LocalDCStatusChangeLevel.NOTABLE_CHANGE);
		}
		else {
			return new Feedback(LocalDCStatusChangeLevel.SMALL_CHANGE);
		}
		
	}
}
