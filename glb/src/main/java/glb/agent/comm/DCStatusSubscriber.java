package glb.agent.comm;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DCStatusSubscriber {

	private TopicConnection conn = null;
    private TopicSession session = null;
    private Topic topic = null;
    private TopicSubscriber recv = null;
    private String datacenterId;
    
    public DCStatusSubscriber (String datacenterId, Hashtable<?, ?> environment, DCStatusUpdateListener statusUpdateListener) throws NamingException, JMSException {
    	this.datacenterId = datacenterId;
    	setupSubscriber(environment, statusUpdateListener);
    }

	public void setupSubscriber(Hashtable<?, ?> environment, DCStatusUpdateListener stateUpdateListener) throws NamingException, JMSException {
		InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
        conn = tcf.createTopicConnection();
        topic = (Topic) iniCtx.lookup("jms/state/" + datacenterId);
        session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        conn.start();
        recv = session.createSubscriber(topic);
        setStateUpdateListener(stateUpdateListener);
	}

	public void setStateUpdateListener(DCStatusUpdateListener stateUpdateListener) throws JMSException {
		recv.setMessageListener(stateUpdateListener);
	}
}
