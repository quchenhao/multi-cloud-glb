package glb.agent.handler;

import java.util.Collection;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.DCStatus;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.decision.LoadDistributionPlanGenerator;
import glb.agent.event.Event;
import glb.agent.event.RedistributionEvent;

public class OverloadEventHandler extends EventHandler {
	
	private LoadDistributionPlanGenerator overloadHandlingPlanGenerator;
	private Logger log = LogManager.getLogger(OverloadEventHandler.class);
	
	public OverloadEventHandler(LoadDistributionPlanGenerator overloadHandlingPlanGenerator) {
		this.overloadHandlingPlanGenerator = overloadHandlingPlanGenerator;
	}

	@Override
	public void handle(Event event) {
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		Collection<DCStatus> remoteDCStatuses = dcManager.getRemoteDCStatuses();
		LoadDistributionPlan overloadHandlingPlan = overloadHandlingPlanGenerator.generateOverloadHandlingPlan(localDCStatus, remoteDCStatuses);
		
		log.info("About to redistribute - " + overloadHandlingPlan.toString());
		
		RedistributionEvent redistributionEvent = new RedistributionEvent(overloadHandlingPlan);
		Queue<Event> eventQueue = EventQueue.getEventQueue();
		eventQueue.add(redistributionEvent);
		eventQueue.notifyAll();
	}

}
