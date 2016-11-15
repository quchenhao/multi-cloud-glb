package glb.agent.comm;

import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

public class DCStatusSubscriber {

	private TopicConnection conn = null;
    private TopicSession session = null;
    private Topic topic = null;
    private TopicSubscriber recv = null;
    private String datacenterId;
    
    public DCStatusSubscriber (String datacenterId, Hashtable<?, ?> environment, DCStatusUpdateListener statusUpdateListener) throws NamingException, JMSException, MalformedURLException {
    	this.datacenterId = datacenterId;
    	setupSubscriber(environment, statusUpdateListener);
    }

	public void setupSubscriber(Hashtable<?, ?> environment, DCStatusUpdateListener stateUpdateListener) throws NamingException, JMSException, MalformedURLException {
		InitialContext iniCtx = new InitialContext(environment);
        Object tmp = iniCtx.lookup("JmsTopicConnectionFactory");
        TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
        conn = tcf.createTopicConnection();
        try{
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
        recv = session.createSubscriber(topic);
        setStateUpdateListener(stateUpdateListener);
	}

	public void setStateUpdateListener(DCStatusUpdateListener stateUpdateListener) throws JMSException {
		recv.setMessageListener(stateUpdateListener);
	}
}
