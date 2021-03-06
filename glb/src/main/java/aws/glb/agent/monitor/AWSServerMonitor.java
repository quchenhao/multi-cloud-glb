package aws.glb.agent.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SummaryStatus;
import com.amazonaws.services.ec2.model.Tag;

import aws.util.AWSStatus;
import glb.agent.core.dc.Server;
import glb.agent.core.dc.ServerStatus;
import glb.agent.core.dc.ServerType;
import glb.agent.monitor.ServerMonitor;

public class AWSServerMonitor extends ServerMonitor{
	
	private AmazonEC2Client ec2Client;
	private String dcId;

	AWSServerMonitor(AWSCredentials credentials, String tagHead, Map<String, ServerType> serverTypes, int port, String dcId) {
		super(tagHead, serverTypes, port);
		this.ec2Client = new AmazonEC2Client(credentials);
		this.dcId = dcId;
	}
	
	@Override
	protected Collection<Server> getServers() {
		
		DescribeInstancesResult result = ec2Client.describeInstances();
		
		Collection<Server> servers = new ArrayList<Server>();
		Map<String, Server> serverMap = new HashMap<String, Server>();
		for (Reservation reservation : result.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				if (instance.getPlacement().getAvailabilityZone().startsWith(dcId)) {
					List<Tag> tags = instance.getTags();
					String name = "";
					for (Tag tag : tags) {
						if (tag.getKey().equals("Name")) {
							name = tag.getValue();
							break;
						}
					}
					if (name.startsWith(tagHead)) {
						String instanceId = instance.getInstanceId();
						String type = instance.getInstanceType();
						ServerType serverType = serverTypes.get(type);
						ServerStatus serverStatus = AWSStatus.getServerSatus(instance.getState().getCode());
						Server server = new Server(instanceId, serverType, instance.getPrivateIpAddress(), port);
						server.setServerStatus(serverStatus);
						servers.add(server);
						serverMap.put(instanceId, server);
					}
				}
			}
		}
		
		DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
		request.setInstanceIds(serverMap.keySet());
		DescribeInstanceStatusResult statusResult = ec2Client.describeInstanceStatus(request);
		
		for (InstanceStatus instanceStatus : statusResult.getInstanceStatuses()) {
			String instanceId = instanceStatus.getInstanceId();
			Server server = serverMap.get(instanceId);
			String status = instanceStatus.getInstanceStatus().getStatus();
			server.setIsHealthey(!status.equals(SummaryStatus.Impaired.toString()));
		}
		
		return servers;
	}

}
