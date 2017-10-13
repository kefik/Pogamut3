package cz.cuni.amis.pogamut.base.agent.jmx.proxy;

import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.jmx.DynamicMBeanToFolderAdapter;
import cz.cuni.amis.introspection.jmx.DynamicProxy;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.jmx.FolderToIJMXEnabledAdapter;
import cz.cuni.amis.pogamut.base.utils.jmx.flag.FlagJMXProxy;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.jmx.AgentLoggerJMXProxy;
import cz.cuni.amis.utils.Lazy;
import cz.cuni.amis.utils.exception.PogamutJMXException;
import cz.cuni.amis.utils.flag.ImmutableFlag;

// TODO: [RUDA] finish implementation
/**
 * Makes it possible to control agent running in remote JVM through JMX protocol.
 * @author ik
 */
public class AgentJMXProxy implements IAgent {

    FlagJMXProxy<IAgentState> agentFlag = null;
    DynamicProxy proxy = null;
    private Lazy<IAgentLogger> agentLogger = new Lazy<IAgentLogger>() {
        @Override
        protected IAgentLogger create() {
        	// TODO: jak je to s agentname a jmx? je treba mit AgentNameJMXProxy?
            return new AgentLoggerJMXProxy(new AgentId(), mbsc, agentName);
        }
    };
    private Lazy<Folder> folder = new Lazy<Folder>() {
    	@Override
        protected Folder create() {
            try {
                ObjectName introspectionRoot = FolderToIJMXEnabledAdapter.getFolderObjectNameForParent(agentName, AbstractAgent.INTROSPECTION_ROOT_NAME);
                DynamicProxy proxy = new DynamicProxy(introspectionRoot, mbsc);
                // convert the proxy to standard Folder
                return new DynamicMBeanToFolderAdapter(proxy);
            } catch (MalformedObjectNameException ex) {
                throw new RuntimeException(ex);
            }
        }
    };
    
    private MBeanServerConnection mbsc = null;
    private ObjectName agentName = null;
	private AgentIdJMXProxy agentId;

    public AgentJMXProxy(String agentJmxAddress) {
        // connect through RMI and get the proxy
        String[] strs = agentJmxAddress.split("\\" + AgentJMXComponents.JMX_SERVER_AGENT_NAME_DELIM);
        String jmxService = strs[0];
        String objectName = strs[1];
        try {
	        JMXServiceURL url = new JMXServiceURL(jmxService);
	        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
	        mbsc = jmxc.getMBeanServerConnection();
	        agentName = ObjectName.getInstance(objectName);
	        proxy = new DynamicProxy(agentName, mbsc);
	        agentFlag = new FlagJMXProxy<IAgentState>(agentName, mbsc, AgentMBeanAdapter.AGENT_STATE_FLAG_NAME);
	        agentId = new AgentIdJMXProxy(this);
        } catch (Exception e) {
        	throw new PogamutJMXException("Can't create AgentJMXProxy.", e, this);
        }
    }   
    
    public MBeanServerConnection getMBeanServerConnection() {
    	return mbsc;
    }
    
    public ObjectName getObjectName() {
    	return agentName;
    }

    @Override
    public IAgentLogger getLogger() {
        return agentLogger.get();
    }
    
    public Logger getLog() {
    	return getLogger().getCategory(getComponentId().getToken());
    }

    @Override
    public ImmutableFlag<IAgentState> getState() {
        return agentFlag.getImmutable();
    }

    /**
     * All exceptions are wrapped in RuntimeException.
     * @param actionName
     * @return
     */
    protected Object callNoException(String actionName) {
        try {
            return call(actionName);
        } catch (AgentException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * All exceptions are wrapped in RuntimeException.
     * @param actionName
     * @return
     */
    protected Object callNoException(String actionName, Object[] params, String[] sig) {
        try {
            return proxy.invoke(actionName, params, sig);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Object call(String actionName) throws AgentException {
        try {
            return proxy.invoke(actionName, null, null);
        } catch (Exception ex) {
            throw new AgentException("JMX communication exception. Error executing method :" + actionName + ".", ex, this);
        }
    }

    protected Object getAttributeNoException(String atr) {
        try {
            return proxy.getAttribute(atr);
        } catch (Exception ex) {
            throw new RuntimeException("Atribute retrieval error.", ex);
        }
    }

    @Override
    public void start() throws AgentException {
        call("start");
    }
    
    @Override
	public void startPaused() throws ComponentCantStartException {
		call("startPaused");
	}

    @Override
    public void pause() throws AgentException {
        call("pause");
    }

    @Override
    public void resume() throws AgentException {
        call("resume");
    }

    @Override
    public void stop() {
        callNoException("stop");
    }

    @Override
    public void kill() {
        callNoException("kill");
    }

    @Override
    public IAgentId getComponentId() {
        return agentId;
    }
    
    public String getName() {
    	return getComponentId().getName().getFlag();
    }

    @Override
    public Folder getIntrospection() {
        return folder.getVal();
    }

	@Override
	public IComponentBus getEventBus() {
		throw new UnsupportedOperationException("not supported");
	}

}