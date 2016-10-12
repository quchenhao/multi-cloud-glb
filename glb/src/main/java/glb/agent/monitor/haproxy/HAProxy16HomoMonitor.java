package glb.agent.monitor.haproxy;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;


import glb.agent.monitor.Measure;
import glb.agent.monitor.Monitor;


public class HAProxy16HomoMonitor extends Monitor {
	
	private HttpGet request;
	private HttpClient client;
	private boolean healthCheck;
	private int unitCapacity;
	private String localTag;
	private int adjust;
	
	
	public HAProxy16HomoMonitor(String address, int port, String url, String user, String password, boolean healthCheck, int unitCapacity, String localTag) {
		String get = "http://" + address + ":" + port + "/" + url + ";csv";
		this.healthCheck = healthCheck;
		this.unitCapacity = unitCapacity;
		this.localTag = localTag;
		this.request = new HttpGet(get);
		
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
		provider.setCredentials(AuthScope.ANY, creds);
		this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
	}
	
	@Override
	public synchronized Measure monitor() throws ClientProtocolException, IOException {
		
		HttpResponse response = client.execute(request);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		reader.readLine();
		
		String frontend = reader.readLine();
		
		String parts[] = frontend.split(",");
		
		int load = Integer.parseInt(parts[46]) + adjust;
		
		String line;
		
		int capacity = 0;
		
		while ((line = reader.readLine()) != null) {
			
			parts = line.split(",");
			
			if (parts[1].equals("BACKEND")) {
				break;
			}
			
			String name = parts[1];
			
			if (name.startsWith(localTag)) {
				if (healthCheck) {
					if (parts[17].equals("UP")) {
						capacity += unitCapacity;
					}
				}
				else {
					capacity += unitCapacity;
				}
			}
		}
		
		return new Measure(capacity, load);
	}

	@Override
	public synchronized void calibrate(int adjust) {
		this.adjust = adjust;
	}

}
