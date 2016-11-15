package glb.agent.comm;

import java.util.Map;
import java.util.Queue;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glb.agent.core.EventQueue;
import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.RemoteDCStatus;
import glb.agent.event.Event;
import glb.agent.event.RemoteDCStatusUpdateEvent;

public class DefaultDCStatusUpdateListener extends DCStatusUpdateListener {
	
	private Logger log = LogManager.getLogger(DCStatusUpdateListener.class);

	@Override
	public void onMessage(Message message) {
		try {
			String dcId = message.getStringProperty("dc_id");
			int maxServiceRate = message.getIntProperty("max_service_rate");
			int capacity = message.getIntProperty("capacity");
			int totalLoad = message.getIntProperty("total_load");
			@SuppressWarnings("unchecked")
			Map<String, Integer> outSourcedLoad = (Map<String, Integer>)message.getObjectProperty("outsourced_load");
			
			DCManager dcManager = DCManager.getDCManager();
			
			RemoteDCStatus remoteDCStatus = dcManager.getRemoteDCStatus(dcId);
			remoteDCStatus.update(maxServiceRate, capacity, totalLoad, outSourcedLoad);
			
			RemoteDCStatusUpdateEvent event = new RemoteDCStatusUpdateEvent(dcId);
			Queue<Event> eventQueue = EventQueue.getEventQueue();
			
			synchronized(eventQueue) {
				eventQueue.add(event);
				eventQueue.notifyAll();
			}
		} catch (JMSException e) {
			log.catching(e);
		}
		
	}

}
