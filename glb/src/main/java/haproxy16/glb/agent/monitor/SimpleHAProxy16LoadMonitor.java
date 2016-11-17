package haproxy16.glb.agent.monitor;


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

import glb.agent.monitor.LoadMonitor;


public class SimpleHAProxy16LoadMonitor extends LoadMonitor {
	
	private HttpGet request;
	private HttpClient client;
	private int adjust;
	
	
	SimpleHAProxy16LoadMonitor(String address, int port, String url, String user, String password) {
		String get = "http://" + address + ":" + port + "/" + url + ";csv";
		this.request = new HttpGet(get);
		
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
		provider.setCredentials(AuthScope.ANY, creds);
		this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
	}
	
	@Override
	public synchronized int getLoad() throws ClientProtocolException, IOException {
		
		HttpResponse response = client.execute(request);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		reader.readLine();
		
		String frontend = reader.readLine();
		
		String parts[] = frontend.split(",");
		
		int load = Integer.parseInt(parts[46]) + adjust;
		
		reader.close();
		
		return load;
	}

	@Override
	public synchronized void calibrate(int adjust) {
		this.adjust = adjust;
	}

}
