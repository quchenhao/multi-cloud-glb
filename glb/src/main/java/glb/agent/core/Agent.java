package glb.agent.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.yaml.snakeyaml.Yaml;

import glb.agent.comm.DCStatusPublisher;
import glb.agent.comm.DCStatusSubscriber;
import glb.agent.comm.DCStatusUpdateListener;
import glb.agent.comm.DefaultDCStatusUpdateListener;
import glb.agent.core.dc.DCInfo;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.ServerType;
import glb.agent.decision.LoadDistributionPlanGenerator;
import glb.agent.decision.LoadDistributionPlanGeneratorLoader;
import glb.agent.detector.OverloadDetector;
import glb.agent.detector.OverloadDetectorLoader;
import glb.agent.event.EventType;
import glb.agent.handler.EventHandler;
import glb.agent.handler.LocalDCStatusUpdateEventHandler;
import glb.agent.handler.OverloadEndEventHandler;
import glb.agent.handler.OverloadEventHandler;
import glb.agent.handler.RedistributionEventHandler;
import glb.agent.handler.RedistributionEventHandlerLoader;
import glb.agent.monitor.LoadMonitor;
import glb.agent.monitor.LoadMonitorLoader;
import glb.agent.monitor.ServerMonitor;
import glb.agent.monitor.ServerMonitorLoader;

public class Agent {
	
	private static Map<String, DCStatusSubscriber> dcStatusSubscribers;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		CmdArgs values = new CmdArgs();
		CmdLineParser parser = new CmdLineParser(values);
		
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("load command line args failed");
			e.printStackTrace();
			System.exit(1);
		}
		
		String yamlPath = values.getYAMLFile();
		
		Yaml yaml = new Yaml();
		Map<String, Object> configs = null;
		try {
			configs = (Map<String, Object>)yaml.load(new FileInputStream(yamlPath));
		} catch (FileNotFoundException e) {
			System.err.println("load " + yamlPath + " failed");
			e.printStackTrace();
			System.exit(1);
		}
		
//		for (Entry<String, Object> entry : configs.entrySet()) {
//			System.out.println(entry.getKey() + entry.getValue().toString());
//		}
		
		Map<String, ServerType> serverTypes = null;
		if (configs.containsKey("server_types")) {
			serverTypes = loadServerTypes((List<Map<String, Object>>)configs.get("server_types"));
		}
		else {
			System.err.println("server_types not found");
			System.exit(1);
		}
		
		String localDCId = null;
		
		if (configs.containsKey("local_dc")) {
			localDCId = ((Map<String, String>)configs.get("local_dc")).get("id");
		}
		else {
			System.err.println("local_dc not found");
			System.exit(1);
		}
		
		Map<String, DCInfo> remoteDCs = null;
		Map<String, Integer> latencies = null;
		
		if (configs.containsKey("remote_dc")) {
			remoteDCs = loadRemoteDCs((List<Map<String, Object>>)configs.get("remote_dc"));
			latencies = loadLatencies((List<Map<String, Object>>)configs.get("remote_dc"));
		}
		else {
			System.err.println("remote_dc not found");
			System.exit(1);
		}
		
		Hashtable<Object, Object> jmsEnvironment = null;
		
		if (configs.containsKey("jms_environment")) {
			jmsEnvironment = new Hashtable<Object, Object>();
			jmsEnvironment.putAll((Map<Object, Object>)configs.get("jms_environment"));
		}
		else {
			System.err.println("jms_environment not found");
			System.exit(1);
		}
		
		RedistributionEventHandler redistributionEventHandler = null;
		
		if (configs.containsKey("redistribution_event_handler")) {
			try {
				redistributionEventHandler = loadRedistributionEventHandler((Map<String, Object>)configs.get("redistribution_event_handler"));
			} catch (Exception e) {
				System.err.println("load redistribution_event_handler failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			System.err.println("redistribution_event_handler not found");
			System.exit(1);
		}
		
		LoadMonitor loadMonitor = null;
		
		if (configs.containsKey("load_monitor")) {
			try {
				loadMonitor = loadLoadMonitor((Map<String, Object>)configs.get("load_monitor"));
			} catch (Exception e) {
				System.err.println("load load_monitor failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			System.err.println("load_monitor not found");
			System.exit(1);
		}
		
		ServerMonitor serverMonitor = null;
		
		if (configs.containsKey("server_monitor")) {
			try {
				serverMonitor = loadServerMonitor((Map<String, Object>)configs.get("server_monitor"), serverTypes);
			} catch (Exception e) {
				System.err.println("load server_monitor failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			System.err.println("server_monitor not found");
			System.exit(1);
		}
		
		int windowSize = 0;
		
		if (configs.containsKey("window")) {
			windowSize = (Integer)((Map<String, Object>)configs.get("window")).get("size");
		}
		else {
			System.err.println("window not found");
			System.exit(1);
		}
		
		OverloadDetector overloadDetector = null;
		
		if (configs.containsKey("overload_detector")) {
			try {
				overloadDetector = loadOverloadDetector((Map<String, Object>)configs.get("overload_detector"));
			} catch (Exception e) {
				System.err.println("load overload_detector failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			System.err.println("overload_detector not found");
			System.exit(1);
		}
		
		LoadDistributionPlanGenerator loadDistributionPlanGenerator = null;
		
		if (configs.containsKey("load_distribution_plan_generator")) {
			try {
				loadDistributionPlanGenerator = loadLoadDistributionPlanGenerator((Map<String, Object>)configs.get("load_distribution_plan_generator"));
			} catch (Exception e) {
				System.err.println("load load_distribution_plan_generator failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			System.err.println("load_distribution_plan_generator not found");
			System.exit(1);
		}
		
		DCManager.initializeDCManager(remoteDCs, latencies, localDCId, windowSize);
		
		Map<EventType, EventHandler> eventHandlers = loadEventHandlers(redistributionEventHandler, overloadDetector, loadDistributionPlanGenerator);
		
		Thread eventHandlingThread = new Thread(new EventHandlingThread(eventHandlers));
		eventHandlingThread.start();
		
		DCStatusPublisher publisher = null;
		try {
			publisher = new DCStatusPublisher(localDCId, jmsEnvironment);
		} catch (Exception e) {
			System.err.println("load dc_status_publisher failed");
			e.printStackTrace();
			System.exit(1);
		}
		
		dcStatusSubscribers = new HashMap<String, DCStatusSubscriber>();
		DCStatusUpdateListener dcStatusUpdateListener = new DefaultDCStatusUpdateListener();
		
		for (DCInfo dcInfo : remoteDCs.values()) {
			try {
				DCStatusSubscriber dcStatusSubscriber = new DCStatusSubscriber(dcInfo.getDcId(), jmsEnvironment, dcStatusUpdateListener);
				dcStatusSubscribers.put(dcInfo.getDcId(), dcStatusSubscriber);
			} catch (NamingException | JMSException e) {
				System.err.println("load dc_status_subscriber for dc " + dcInfo.getDcId() + " failed");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		Thread dcStatusUpdateThread = new Thread(new DCStatusUpdateThread(publisher));
		dcStatusUpdateThread.start();
		
		Thread loadMonitorThread = new Thread(new MonitorThread(loadMonitor, 2000));
		loadMonitorThread.start();
		
		Thread serverMonitorThread = new Thread(new MonitorThread(serverMonitor, 2000));
		
		serverMonitorThread.start();
	}

	private static LoadDistributionPlanGenerator loadLoadDistributionPlanGenerator(Map<String, Object> map) throws Exception{
		String loaderClass = (String)map.get("loader");
		LoadDistributionPlanGeneratorLoader loader = (LoadDistributionPlanGeneratorLoader) Class.forName(loaderClass).newInstance();
		return loader.load(map);
	}

	private static OverloadDetector loadOverloadDetector(Map<String, Object> map) throws Exception {
		String loaderClass = (String)map.get("loader");
		OverloadDetectorLoader loader = (OverloadDetectorLoader) Class.forName(loaderClass).newInstance();
		return loader.load(map);
	}

	private static Map<EventType, EventHandler> loadEventHandlers(RedistributionEventHandler redistributionEventHandler, OverloadDetector overloadDetector, LoadDistributionPlanGenerator loadDistributionPlanGenerator) {
		Map<EventType, EventHandler> eventHandlers = new HashMap<EventType, EventHandler>();
		EventHandler localDCStatusUpdateEventHandler = new LocalDCStatusUpdateEventHandler(overloadDetector);
		eventHandlers.put(EventType.LocalDCStatusUpdateEvent, localDCStatusUpdateEventHandler);
		EventHandler overloadEndEventHandler = new OverloadEndEventHandler();
		eventHandlers.put(EventType.OverloadEndEvent, overloadEndEventHandler);
		EventHandler overloadEvent = new OverloadEventHandler(loadDistributionPlanGenerator);
		eventHandlers.put(EventType.OverloadEvent, overloadEvent);
		eventHandlers.put(EventType.RedistributionEvent, redistributionEventHandler);
		return eventHandlers;
	}

	private static Map<String, Integer> loadLatencies(List<Map<String, Object>> list) {
		Map<String, Integer> latencies = new HashMap<String, Integer>();
		
		for (Map<String, Object> map : list) {
			String dcId = (String)map.get("id");
			int latency = (Integer)map.get("latency");
			latencies.put(dcId, latency);
		}
		return latencies;
	}

	private static ServerMonitor loadServerMonitor(Map<String, Object> map, Map<String, ServerType> serverTypes) throws Exception {
		String loaderClass = (String)map.get("loader");
		ServerMonitorLoader loader = (ServerMonitorLoader) Class.forName(loaderClass).newInstance();
		return loader.load(map, serverTypes);
	}

	private static LoadMonitor loadLoadMonitor(Map<String, Object> map) throws Exception{
		String loaderClass = (String)map.get("loader");
		LoadMonitorLoader loader = (LoadMonitorLoader) Class.forName(loaderClass).newInstance();
		return loader.load(map);
	}

	private static RedistributionEventHandler loadRedistributionEventHandler(Map<String, Object> map) throws Exception {
		String loaderClass = (String)map.get("loader");
		RedistributionEventHandlerLoader loader = (RedistributionEventHandlerLoader)Class.forName(loaderClass).newInstance();
		
		return loader.load(map);
	}

	private static Map<String, DCInfo> loadRemoteDCs(List<Map<String, Object>> list) {
		Map<String, DCInfo> remoteDCs = new HashMap<String, DCInfo>();
		
		for (Map<String, Object> map : list) {
			String dcId = (String)map.get("id");
			String address = (String)map.get("address");
			int port = (Integer)map.get("port");
			DCInfo dcInfo = new DCInfo(dcId, address, port);
			remoteDCs.put(dcId, dcInfo);
		}
		return remoteDCs;
	}

	private static Map<String, ServerType> loadServerTypes(List<Map<String, Object>> list) {
		Map<String, ServerType> serverTypes = new HashMap<String, ServerType>();
		for (Map<String, Object> map : list) {
			String type = (String)map.get("type");
			int capacity = (Integer)map.get("capacity");
			int serviceRate = (Integer)map.get("service_rate");
			ServerType serverType = new ServerType(type, serviceRate, capacity);
			serverTypes.put(type, serverType);
		}
		return serverTypes;
	}
}
