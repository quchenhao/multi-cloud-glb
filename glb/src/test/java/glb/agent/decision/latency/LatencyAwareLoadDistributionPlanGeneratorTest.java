package glb.agent.decision.latency;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.RemoteDCStatus;
import glb.agent.decision.LoadDistributionPlan;

public class LatencyAwareLoadDistributionPlanGeneratorTest {

	private LatencyAwareLoadDistributionPlanGenerator planGenerator;
	@Test
	public void test() throws Exception {
		planGenerator = new LatencyAwareLoadDistributionPlanGenerator();
		
		testNormal();
		testOutSourced();
		testCorner();
	}
	private void testOutSourced() throws Exception {
		LocalDCStatus localDCStatus = new LocalDCStatus("virginia", 1);
		localDCStatus.updateLoad(50);
		Map<String, Integer> outSourcedLoad = new HashMap<String, Integer>();
		outSourcedLoad.put("ireland", 15);
		outSourcedLoad.put("tokyo", 15);
		localDCStatus.updateOutSourcedLoad(outSourcedLoad);
		Collection<RemoteDCStatus> remoteDCStatuses = new ArrayList<RemoteDCStatus>();
		
		RemoteDCStatus ireland = new RemoteDCStatus("ireland", 76);
		ireland.updateCapacity(100);
		ireland.updateMaxServiceRate(120);
		ireland.updateLoad(70);
		remoteDCStatuses.add(ireland);
		
		RemoteDCStatus tokyo = new RemoteDCStatus("tokyo", 167);
		tokyo.updateCapacity(120);
		tokyo.updateMaxServiceRate(130);
		tokyo.updateLoad(80);
		remoteDCStatuses.add(tokyo);
		
		LoadDistributionPlan plan = planGenerator.generateOverloadHandlingPlan(localDCStatus, remoteDCStatuses);
		assertTrue(plan.getLoadToReject() == 0);
		Map<String, Integer> outSourcePlan = plan.getOutSourcePlan();
		
		for (Entry<String, Integer> entry : outSourcePlan.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
	}
	private void testCorner() throws Exception {
		LocalDCStatus localDCStatus = new LocalDCStatus("virginia", 1);
		localDCStatus.updateLoad(50);
		Collection<RemoteDCStatus> remoteDCStatuses = new ArrayList<RemoteDCStatus>();
		
		RemoteDCStatus ireland = new RemoteDCStatus("ireland", 76);
		ireland.updateCapacity(100);
		ireland.updateMaxServiceRate(120);
		ireland.updateLoad(70);
		remoteDCStatuses.add(ireland);
		
		RemoteDCStatus tokyo = new RemoteDCStatus("tokyo", 167);
		tokyo.updateCapacity(120);
		tokyo.updateMaxServiceRate(130);
		tokyo.updateLoad(120);
		remoteDCStatuses.add(tokyo);
		
		LoadDistributionPlan plan = planGenerator.generateOverloadHandlingPlan(localDCStatus, remoteDCStatuses);
		assertTrue(plan.getLoadToReject() == 20);
		Map<String, Integer> outSourcePlan = plan.getOutSourcePlan();
		
		for (Entry<String, Integer> entry : outSourcePlan.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	private void testNormal() throws Exception {
		LocalDCStatus localDCStatus = new LocalDCStatus("virginia", 1);
		localDCStatus.updateLoad(50);
		Collection<RemoteDCStatus> remoteDCStatuses = new ArrayList<RemoteDCStatus>();
		
		RemoteDCStatus ireland = new RemoteDCStatus("ireland", 76);
		ireland.updateCapacity(100);
		ireland.updateMaxServiceRate(120);
		ireland.updateLoad(70);
		remoteDCStatuses.add(ireland);
		
		RemoteDCStatus tokyo = new RemoteDCStatus("tokyo", 167);
		tokyo.updateCapacity(120);
		tokyo.updateMaxServiceRate(130);
		tokyo.updateLoad(80);
		remoteDCStatuses.add(tokyo);
		
		LoadDistributionPlan plan = planGenerator.generateOverloadHandlingPlan(localDCStatus, remoteDCStatuses);
		assertTrue(plan.getLoadToReject() == 0);
		Map<String, Integer> outSourcePlan = plan.getOutSourcePlan();
		
		for (Entry<String, Integer> entry : outSourcePlan.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

}
