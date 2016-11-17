package glb.agent.comm;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DCStatusUpdateThread implements Runnable{

	private DCStatusPublisher dcStatusPublisher;
	private PublishReminder publishReminder;
	private Logger log = LogManager.getLogger(DCStatusUpdateThread.class);
	
	public DCStatusUpdateThread(DCStatusPublisher dcStatusPublisher, PublishReminder publishReminder) {
		this.dcStatusPublisher = dcStatusPublisher;
		this.publishReminder = publishReminder;
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			try {
				dcStatusPublisher.publishStatus();
				log.trace("Local DC status published");
				synchronized(publishReminder) {
					publishReminder.wait(60000);
				}
			} catch (JMSException | InterruptedException e ) {
				log.catching(e);
			}
		}
	}

}
