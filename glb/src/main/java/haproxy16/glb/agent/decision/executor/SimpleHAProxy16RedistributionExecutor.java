package haproxy16.glb.agent.decision.executor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import glb.agent.core.dc.DCInfo;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.Server;
import glb.agent.decision.LoadDistributionPlan;
import glb.agent.decision.executor.RedistributionExecutor;
import haproxy16.glb.agent.decision.executor.weight.HAProxy16WeightCalculator;
import haproxy16.glb.agent.decision.executor.weight.WeightTable;

public class SimpleHAProxy16RedistributionExecutor extends RedistributionExecutor {

	private String baseFilePath;
	private Runtime runtime;
	private HAProxy16WeightCalculator weightCalculator;

	SimpleHAProxy16RedistributionExecutor(HAProxy16WeightCalculator weightCalculator, String baseFilePath) {
		runtime = Runtime.getRuntime();
		this.weightCalculator = weightCalculator;
		this.baseFilePath = baseFilePath;
	}

	public SimpleHAProxy16RedistributionExecutor(String baseFilePath) {
	}

	@Override
	public void redistribute(LoadDistributionPlan loadDistributionPlan) throws Exception {

		Process process = runtime.exec("cp " + baseFilePath + " haproxy.cfg");
		process.waitFor();
		File file = new File("haproxy.cfg");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		int loadToReject = loadDistributionPlan.getLoadToReject();
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();

		if (loadToReject > 0) {
			writer.append("acl white_list src localhost\n");
			writer.append("acl test rand(" + localDCStatus.getCapacity() + ") lt " + loadToReject + "\n");
			writer.append("tcp-request content reject if test !white_list\n");

		}

		Collection<Server> healthyServers = localDCStatus.getAllHealthyServers();
		Map<String, Integer> outSourcedLoad = loadDistributionPlan.getOutSourcePlan();

		WeightTable weightTable = weightCalculator.calculateWeight(healthyServers, outSourcedLoad);

		for (Server server : healthyServers) {
			String serverId = server.getServerId();
			writer.append("server " + serverId + " " + server.getAddress() + ":" + server.getPort() + " weight "
					+ weightTable.getServerWeight(serverId) + "\n");
		}

		for (String dcId : outSourcedLoad.keySet()) {
			DCInfo dcInfo = dcManager.getDCInfo(dcId);
			writer.append("server " + dcId + " " + dcInfo.getAddress() + ":" + dcInfo.getPort() + " weight "
					+ weightTable.getRemoteDCWeight(dcId) + "\n");
		}

		writer.close();

		process = runtime.exec("sh reconfig.sh");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;
		String out = "";
		while ((line = bReader.readLine()) != null) {
			out += line + "\n";
		}

		bReader.close();

		if (!out.equals("")) {
			throw new RuntimeException(out);
		}

		process.waitFor();

	}

}
