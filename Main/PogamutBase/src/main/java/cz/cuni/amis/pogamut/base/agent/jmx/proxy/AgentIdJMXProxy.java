package cz.cuni.amis.pogamut.base.agent.jmx.proxy;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MalformedObjectNameException;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base.utils.jmx.flag.FlagJMXProxy;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Makes it possible to control agent running in remote JVM through JMX protocol.
 * @author ik
 */
public class AgentIdJMXProxy implements IAgentId {

	FlagJMXProxy<String> agentNameFlag = null;    
	private AgentJMXProxy agentProxy;
	
	private Token token;

    public AgentIdJMXProxy(AgentJMXProxy agentProxy) throws MalformedURLException, IOException, MalformedObjectNameException {
    	this.agentProxy = agentProxy;
        agentNameFlag = new FlagJMXProxy<String>(agentProxy.getObjectName(), agentProxy.getMBeanServerConnection(), AgentMBeanAdapter.AGENT_NAME_FLAG_NAME);
        token = Tokens.get((String)agentProxy.getAttributeNoException("ComponentId"));
    }

	@Override
	public Flag<String> getName() {
		return agentNameFlag;
	}

	@Override
	public String getToken() {
		return token.getToken();
	}

	@Override
	public long[] getIds() {
		return token.getIds();
	}
   
}