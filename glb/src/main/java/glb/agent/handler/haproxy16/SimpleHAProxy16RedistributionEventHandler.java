package glb.agent.handler.haproxy16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;
import glb.agent.core.dc.Server;
import glb.agent.decision.OverloadHandlingPlan;
import glb.agent.handler.RedistributionEventHandler;

public class SimpleHAProxy16RedistributionEventHandler extends RedistributionEventHandler{
	
	private String baseFilePath;
	private Runtime runtime;
	
	public SimpleHAProxy16RedistributionEventHandler() {
		runtime = Runtime.getRuntime();
	}
	
	public SimpleHAProxy16RedistributionEventHandler(String baseFilePath) {}

	@Override
	protected void redistribute(OverloadHandlingPlan overloadHandlingPlan) {
		
		try {
			Process process = runtime.exec("cp " + baseFilePath + " haproxy.cfg");
			process.waitFor();
			File file = new File("haproxy.cfg");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			int loadToReject = overloadHandlingPlan.getLoadToReject();
			DCManager dcManager = DCManager.getDCManager();
			LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
			
			if (loadToReject > 0) {
				writer.append("acl white_list src localhost\n");
				writer.append("acl test rand(" + localDCStatus.getCapacity() +") lt " + loadToReject + "\n");
				writer.append("tcp-request content reject if test !white_list\n");
				
			}
			
			Collection<Server> healthyServers = localDCStatus.getAllHealthyServers();
			
			for (Server server : servers) {
				writer.append("server " + server.getServerId() + " " + server.getAddress() + ":" + server.getPort() + " weight " + server.getCapacity() + "\n");
			}
			
			for (Server backup : backUps) {
				writer.append("server " + backup.getServerId() + " " + backup.getAddress() + ":" + backup.getPort() + " weight " + backup.getCapacity() + "\n");
			}
			
			writer.close();
			
			process = runtime.exec("sh reconfig.sh");
			BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			while ((line = bReader.readLine()) != null) {
				System.out.println(line);
			}
			process.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
}
