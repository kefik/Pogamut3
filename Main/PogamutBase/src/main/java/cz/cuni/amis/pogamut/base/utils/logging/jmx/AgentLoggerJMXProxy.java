package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import cz.cuni.amis.introspection.jmx.DynamicProxy;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.utils.logging.AbstractAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IJMXAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.ILogCategories;
import cz.cuni.amis.utils.Lazy;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Makes remote AgentLogger look like local logger. The communication is handled
 * through JMX interface.
 * <p><p>
 * {@link IJMXAgentLogger} interface is fully proxied (executes JMX remote calls).
 * 
 * @author ik
 */
public class AgentLoggerJMXProxy extends AbstractAgentLogger {

    Lazy<ILogCategories> logCategories = new Lazy<ILogCategories>() {
        @Override
        protected ILogCategories create() {
            return new LogCategoriesJMXProxy(mbsc, parentName);
        }
    };
    MBeanServerConnection mbsc = null;
    ObjectName parentName = null;
    
    DynamicProxy agentLoggerProxy;

    public AgentLoggerJMXProxy(IAgentId agentName, MBeanServerConnection mbsc, ObjectName parentName) {
        super(agentName);
        this.mbsc = mbsc;
        this.parentName = parentName;
        ObjectName objectName = AgentLogger.getJMXAgentLoggerName(parentName);        
        this.agentLoggerProxy = new DynamicProxy(objectName, mbsc); 
    }
    ILogCategories cats = null;

    @Override
    protected ILogCategories getLogCategories() {
        return logCategories.getVal();
    }

    @Override
    public void enableJMX(MBeanServer mBeanServer, ObjectName parent) throws JMXAlreadyEnabledException, CantStartJMXException {
        throw new UnsupportedOperationException(
                "This logger is already a proxy to some remote logger. Making two proxies isn't the best practice, however it can be done, "
                + "just implement this method based on DefaultAgentLogger's implementation of JMX.");
    }
    
    /**
     * Executes JMX remote call to the proxied {@link IAgentLogger#getNetworkLoggerHost()} method.
     * 
     * @see IAgentLogger#getNetworkLoggerHost()
     */
    @Override
    public String getNetworkLoggerHost() {
    	try {
			return (String) agentLoggerProxy.getAttribute("NetworkLoggerHost");
		} catch (Exception e) {
			// TODO: [Jimmy/Ruda/Honza] Create specific JMX exceptions
			throw new PogamutException("Could not invoke agentLoggerProxy.getAttribute(\"NetworkLoggerHost\").", e, this); 
		}
    }
    
    /**
     * Executes JMX remote call to the proxied {@link IAgentLogger#getNetworkLoggerPort()} method.
     * 
     * @see IAgentLogger#getNetworkLoggerPort()
     */
    @Override
    public Integer getNetworkLoggerPort() {
    	try {
			return (Integer) agentLoggerProxy.getAttribute("NetworkLoggerPort");
		} catch (Exception e) {
			// TODO: [Jimmy/Ruda/Honza] Create specific JMX exceptions
			throw new PogamutException("Could not invoke agentLoggerProxy.getAttribute(\"NetworkLoggerPort\").", e, this); 
		}
    }
    
    /**
     * Executes JMX remote call to the proxied {@link IAgentLogger#addDefaultNetworkHandler()} method.
     * 
     * @see IAgentLogger#addDefaultNetworkHandler()
     */
    @Override
    public synchronized void addDefaultNetworkHandler() {
    	try {
			agentLoggerProxy.invoke("addDefaultNetworkHandler", null, null);
		} catch (Exception e) {
			// TODO: [Jimmy/Ruda/Honza] Create specific JMX exceptions
			throw new PogamutException("Could not invoke agentLoggerProxy.invoke(\"addDefaultNetworkHandler\").", e, this); 
		}
    }
    
    /**
     * Executes JMX remote call to the proxied {@link IAgentLogger#removeDefaultNetworkHandler()} method.
     * 
     * @see IAgentLogger#removeDefaultNetworkHandler()
     */
    public synchronized void removeDefaultNetworkHandler() {
    	try {
			agentLoggerProxy.invoke("removeDefaultNetworkHandler", null, null);
		} catch (Exception e) {
			// TODO: [Jimmy/Ruda/Honza] Create specific JMX exceptions
			throw new PogamutException("Could not invoke agentLoggerProxy.invoke(\"removeDefaultNetworkHandler\").", e, this); 
		}
    }
    
    /**
     * Executes JMX remote call to the proxied {@link IAgentLogger#isDefaultNetworkHandler()} method.
     * 
     * @see IAgentLogger#isDefaultNetworkHandler()
     */
    @Override
	public boolean isDefaultNetworkHandler() {
    	try {
			return (Boolean) agentLoggerProxy.getAttribute("DefaultNetworkHandler");
		} catch (Exception e) {
			// TODO: [Jimmy/Ruda/Honza] Create specific JMX exceptions
			throw new PogamutException("Could not invoke agentLoggerProxy.getAttribute(\"DefaultNetworkHandler\").", e, this);
		}
    }
    
}
