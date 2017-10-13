package cz.cuni.amis.pogamut.base.agent.jmx.adapter;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.utils.jmx.flag.JMXFlagDecorator;

/**
 * Class for exporting arbitrary agents as managed MBeans.
 * @author Ik
 */
public class AgentMBeanAdapter<T extends IAgent> implements IAgentMBeanAdapter {

    public static final String AGENT_STATE_FLAG_NAME = "agent_state_flag";

	public static final String AGENT_NAME_FLAG_NAME = "agent_name_flag";
    
    T agent = null;
    
    JMXFlagDecorator<IAgentState> jmxAgentState = null;
    JMXFlagDecorator<String> jmxAgentName = null;


    public AgentMBeanAdapter(T agent, ObjectName objectName, MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        this.agent = agent;
        jmxAgentState = new JMXFlagDecorator<IAgentState>(agent.getState(), objectName, mbs, AGENT_STATE_FLAG_NAME);
        jmxAgentName = new JMXFlagDecorator<String>(agent.getComponentId().getName(), objectName, mbs, AGENT_NAME_FLAG_NAME);
    }

    protected T getAgent() {
        return agent;
    }

    @Override
    public ObjectName getObjectName(String domain) throws MalformedObjectNameException {
        return ObjectName.getInstance(domain + ":name=" + agent.getComponentId().getToken().replace(".", "_") + ",type=agent");
    }

    @Override
    public String getComponentId() {
        return agent.getComponentId().getToken();
    }

    @Override
    public IAgentState getState() {
        return agent.getState().getFlag();
    }
    
    @Override
    public void start() throws AgentException {
        agent.start();
    }

    @Override
    public void pause() throws AgentException {
        agent.pause();
    }

    @Override
    public void resume() throws AgentException {
        agent.resume();
    }

    @Override
    public void stop() throws AgentException {
        agent.stop();
    }

    @Override
    public void kill() {
        agent.kill();
    }

	@Override
	public String getName() {
		return agent.getComponentId().getName().getFlag();
	}

}