package glb.agent.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.monitor.Monitor;

public class MonitorThread implements Runnable {
	
	private Monitor monitor;
	private int interval;
	private Logger log = LogManager.getLogger(MonitorThread.class);
	public MonitorThread(Monitor monitor, int interval) {
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				monitor.monitor();
				Thread.sleep(interval);
			} catch (Exception e) {
				log.catching(e);
			}
		}
	}

}
