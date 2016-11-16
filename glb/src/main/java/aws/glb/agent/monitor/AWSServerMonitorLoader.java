package aws.glb.agent.monitor;

import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.ServerType;
import glb.agent.monitor.ServerMonitor;
import glb.agent.monitor.ServerMonitorLoader;

public class AWSServerMonitorLoader extends ServerMonitorLoader{

	@Override
	public ServerMonitor load(Map<?, ?> parameters, Map<String, ServerType> serverTypes) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)parameters;
		
		String tagHead = (String)map.get("tag_head");
		int port = (Integer)map.get("port");
		String accessKey = (String)map.get("access_key");
		String secretKey = (String)map.get("secret_key");
		
		AWSCredentials credential = new BasicAWSCredentials(accessKey, secretKey);
		
		String localDCId = DCManager.getDCManager().getLocalDCId();
		
		return new AWSServerMonitor(credential, tagHead, serverTypes, port, localDCId);
	}
}
