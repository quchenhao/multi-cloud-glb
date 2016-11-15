package glb.agent.decision.latency;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
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
	private void testOutSourced() {
		// TODO Auto-generated method stub
		
	}
	private void testCorner() throws Exception {
		
	}
	private void testNormal() throws Exception {
		LocalDCStatus localDCStatus = new LocalDCStatus("Virginia", 1);
		localDCStatus.updateLoad(50);
		Collection<RemoteDCStatus> remoteDCStatuses = new ArrayList<RemoteDCStatus>();
		
		RemoteDCStatus ireland = new RemoteDCStatus("Ireland", 76);
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
		assert plan.getLoadToReject() == 0;
		Map<String, Integer> outSourcePlan = plan.getOutSourcePlan();
		
		for (Entry<String, Integer> entry : outSourcePlan.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

}
