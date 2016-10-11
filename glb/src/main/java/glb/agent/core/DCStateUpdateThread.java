package glb.agent.core;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.comm.DCStatusPublisher;

class DCStateUpdateThread implements Runnable{

	private DCStatusPublisher dcStatusPublisher;
	private Logger log = LogManager.getLogger(DCStateUpdateThread.class);
	
	public DCStateUpdateThread(DCStatusPublisher dcStatusPublisher) {
		this.dcStatusPublisher = dcStatusPublisher;
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			try {
				dcStatusPublisher.publishStatus();
				log.trace("Local DC status published");
				Thread.sleep(60000);
			} catch (JMSException | InterruptedException e ) {
				log.catching(e);
			}
		}
	}

}
