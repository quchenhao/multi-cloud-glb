package haproxy16.glb.agent.handler;

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
import glb.agent.handler.RedistributionEventHandler;
import glb.agent.handler.weight.WeightCalculator;
import glb.agent.handler.weight.WeightTable;

public class SimpleHAProxy16RedistributionEventHandler extends RedistributionEventHandler{
	
	private String baseFilePath;
	private Runtime runtime;
	private WeightCalculator weightCalculator;
	
	SimpleHAProxy16RedistributionEventHandler(WeightCalculator weightCalculator, String baseFilePath) {
		runtime = Runtime.getRuntime();
		this.weightCalculator = weightCalculator;
		this.baseFilePath = baseFilePath;
	}
	
	public SimpleHAProxy16RedistributionEventHandler(String baseFilePath) {}

	@Override
	protected void redistribute(LoadDistributionPlan loadDistributionPlan) {
		
		try {
			Process process = runtime.exec("cp " + baseFilePath + " haproxy.cfg");
			process.waitFor();
			File file = new File("haproxy.cfg");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			int loadToReject = loadDistributionPlan.getLoadToReject();
			DCManager dcManager = DCManager.getDCManager();
			LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
			
			if (loadToReject > 0) {
				writer.append("acl white_list src localhost\n");
				writer.append("acl test rand(" + localDCStatus.getCapacity() +") lt " + loadToReject + "\n");
				writer.append("tcp-request content reject if test !white_list\n");
				
			}
			
			Collection<Server> healthyServers = localDCStatus.getAllHealthyServers();
			Map<String, Integer> outSourcedLoad = loadDistributionPlan.getOutSourcePlan();
			
			WeightTable weightTable = weightCalculator.calculateWeight(healthyServers, outSourcedLoad);
			
			
			for (Server server : healthyServers) {
				String serverId = server.getServerId();
				writer.append("server " + serverId + " " + server.getAddress() + ":" + server.getPort() + " weight " + weightTable.getServerWeight(serverId) + "\n");
			}
			
			for (String dcId : outSourcedLoad.keySet()) {
				DCInfo dcInfo = dcManager.getDCInfo(dcId);
				writer.append("server " + dcId + " " + dcInfo.getAddress() + ":" + dcInfo.getPort() + " weight " + weightTable.getRemoteDCWeight(dcId) + "\n");
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
				log.error(out);
			}
			
			process.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
}
