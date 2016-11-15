package glb.agent.comm;

import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.LocalDCStatus;

public class DCStatusPublisher {

	private String datacenterId;

	private TopicConnection conn = null;
	private TopicSession session = null;
	private Topic topic = null;

	public DCStatusPublisher(String datacenterId, Hashtable<?, ?> environment) throws NamingException, JMSException, MalformedURLException {
		this.datacenterId = datacenterId;
		setupPublisher(environment);
	}

	private synchronized void setupPublisher(Hashtable<?, ?> environment) throws NamingException, JMSException, MalformedURLException {
		InitialContext iniCtx = new InitialContext(environment);
		Object tmp = iniCtx.lookup("JmsTopicConnectionFactory");
		TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
		conn = tcf.createTopicConnection();
		
	    try {
	    	topic = (Topic) iniCtx.lookup("jms/state/" + datacenterId);
	    } catch(Exception e) {
	    	String url = (String)environment.get(Context.PROVIDER_URL);
	    	JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
	    	String newTopic = "jms/state/" + datacenterId;
	    	Boolean isQueue = Boolean.FALSE;
	    	if (!admin.addDestination(newTopic, isQueue)) {
	    		System.err.println("Failed to create topic " + topic);
	    		System.exit(1);
	    	}
	    	topic = (Topic) iniCtx.lookup("jms/state/" + datacenterId);
	    }
		session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		conn.start();
	}

	public synchronized void publishStatus() throws JMSException {
		DCManager dcManager = DCManager.getDCManager();
		LocalDCStatus localDCStatus = dcManager.getLocalDCStatus();
		TopicPublisher send = session.createPublisher(topic);
		TextMessage tm = session.createTextMessage();
		tm.setStringProperty("dc_id", localDCStatus.getDCId());
		tm.setIntProperty("max_service_rate", localDCStatus.getMaxServiceRate());
		tm.setIntProperty("capacity", localDCStatus.getCapacity());
		tm.setIntProperty("total_load", localDCStatus.getTotalLoad());
		tm.setObjectProperty("outsourced_load", localDCStatus.getOutsourcedLoad());
		send.publish(tm);
		send.close();
	}

	public synchronized void destroy() throws JMSException {
		conn.stop();
		session.close();
		conn.close();
	}
}
