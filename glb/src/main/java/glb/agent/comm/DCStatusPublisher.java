package glb.agent.comm;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import glb.agent.core.dc.DCManager;
import glb.agent.core.dc.DCStatus;

public class DCStatusPublisher {

	private String datacenterId;

	private TopicConnection conn = null;
	private TopicSession session = null;
	private Topic topic = null;

	public DCStatusPublisher(String datacenterId, Hashtable<?, ?> environment) throws NamingException, JMSException {
		this.datacenterId = datacenterId;
		setupPublisher(environment);
	}

	private synchronized void setupPublisher(Hashtable<?, ?> environment) throws NamingException, JMSException {
		InitialContext iniCtx = new InitialContext(environment);
		Object tmp = iniCtx.lookup("ConnectionFactory");
		TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
		conn = tcf.createTopicConnection();
		topic = (Topic) iniCtx.lookup("jms/state/" + datacenterId);
		session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		conn.start();
	}

	public synchronized void publishStatus() throws JMSException {
		DCManager dcManager = DCManager.getDCManager();
		DCStatus localDCStatus = dcManager.getLocalDCStatus();
		TopicPublisher send = session.createPublisher(topic);
		TextMessage tm = session.createTextMessage(localDCStatus.toJSONString());
		send.publish(tm);
		send.close();
	}

	public synchronized void destroy() throws JMSException {
		conn.stop();
		session.close();
		conn.close();
	}
}