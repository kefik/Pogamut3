package cz.cuni.amis.pogamut.base.agent.jmx;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutMBeanServer;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ExceptionToString;

/**
 * Wraps a few methods into one place so it won't plague the public method space
 * of the agent. (Make things a bit clear...).
 * <p>
 * <p>
 * Contains list of IJMXEnabled components that should be enabled when the whole
 * JMX feature of the agent is fired up.
 * <p>
 * <p>
 * Note that jmx domain is taken from the java property "pogamut.jmx.domain".
 * 
 * @author Jimmy
 */
@AgentScoped
public class AgentJMXComponents<T extends IAgent> {

	public static final String LOG_CATEGORY_NAME = "AgentJMXComponents";

	/**
	 * Separates JMX server address and the agent's MBean object name in address
	 * exported to the outside.
	 */
	public static final String JMX_SERVER_AGENT_NAME_DELIM = "|";

	/**
	 * MBeanServer the JMX is currently using. (If null the jmx is not enabled.)
	 */
	private PogamutMBeanServer mBeanServer = null;

	/**
	 * Current domain name of the objects that are registered by this agent and
	 * its components.
	 * <p>
	 * <p>
	 * Note that every agent MUST HAVE its own unique domain.
	 */
	private String jmxDomain = null;

	/**
	 * List of IJMXEnabled components that are enabled when enableJMX() is
	 * called.
	 */
	private Set<IJMXEnabled> jmxComponents = new LinkedHashSet<IJMXEnabled>();

	/**
	 * ObjectName of the agent owning this class.
	 */
	private ObjectName agentJMXName = null;

	private IAgentLogger agentLogger;

	private LogCategory log;

	/**
	 * Agent that owns the JMX (equally the agent this object supports).
	 */
	private T agent;

	@Inject
	public AgentJMXComponents(T agent) {
		this.agent = agent;
		this.agentLogger = agent.getLogger();
		this.log = agentLogger.getCategory(LOG_CATEGORY_NAME);
	}

	/**
	 * Adding new IJMXEnabled component to the list - registering it so it will
	 * be notified when the enableJMX() is called.
	 * 
	 * @param component
	 */
	public void addComponent(IJMXEnabled component) {
		synchronized (jmxComponents) {			
			if (jmxComponents.contains(component)) {
				return;
			}
			if (log.isLoggable(Level.FINER)) log.finer("Adding new JMX component " + component);
			jmxComponents.add(component);
			if (isJMXEnabled()) {
				if (log.isLoggable(Level.FINE)) log.fine("Enabling JMX component " + component);
				component.enableJMX(mBeanServer, agentJMXName);
			}
			if (log.isLoggable(Level.INFO)) log.info("New JMX component added: " + component);
		}
	}

	/**
	 * MBeanServer, if null the jmx is not enabled.
	 * 
	 * @return
	 */
	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	/**
	 * JMX domain of the whole agent - used to construct ObjectName instances.
	 * If null the jmx is not enabled.
	 * 
	 * @return
	 */
	public String getJMXDomain() {
		return jmxDomain;
	}

	/**
	 * Whether the JMX is enabled or not.
	 * 
	 * @return
	 */
	public boolean isJMXEnabled() {
		return jmxDomain != null;
	}

	/**
	 * Returns ObjectName of the agent.
	 * 
	 * @return
	 */
	public ObjectName getAgentJMXName() {
		return agentJMXName;
	}

	/**
	 * This enables the JMX feature on the whole agent notifying all IJMXEnabled
	 * components to register itself to provided mBeanServer.
	 * <p>
	 * <p>
	 * Note that jmxDomain must be well-formed in JMX Object Name sense.
	 * 
	 * @param mBeanServer
	 * @param jmxDomain
	 * @return full jmx address of the agent
	 * @throws JMXAlreadyEnabledException
	 * @throws CantStartJMXException
	 */
	public String enableJMX() throws JMXAlreadyEnabledException, CantStartJMXException {
		synchronized (jmxComponents) {
			if (!isJMXEnabled()) {
				this.mBeanServer = new PogamutMBeanServer(Pogamut.getPlatform().getMBeanServer());
				jmxDomain = PogamutJMX.getPogamutJMXDomain();
				try {
					agentJMXName = PogamutJMX.getAgentObjectName(agent.getComponentId());
				} catch (Exception e) {
					throw new CantStartJMXException("Can't create object name for the agent.", e, log, this);
				}
	
				// export the agent itself
				try {
					// create the MBean for agent
					AgentMBeanAdapter agentMBean = createAgentMBean(agentJMXName, mBeanServer);
					mBeanServer.registerMBean(agentMBean, agentJMXName);
				} catch (Exception ex) {
					throw new CantStartJMXException("Agent MBean cannot be registered.", ex, log, this);
				}
	
				if (log.isLoggable(Level.INFO)) log.info("Enabling JMX.");
				int numOk = 0;
				for (IJMXEnabled jmxComponent : jmxComponents) {
					try {
						if (log.isLoggable(Level.FINE)) log.fine("Starting JMX component: " + jmxComponent);
						jmxComponent.enableJMX(mBeanServer, agentJMXName);
						++numOk;
					} catch (JMXAlreadyEnabledException e) {
						log.log(Level.SEVERE, ExceptionToString.process("IJMXEnabled[class="+ jmxComponent.getClass().getName()+ ",name="+ jmxComponent.toString()+ "]: states that it's been already enabled.", e));
					} catch (CantStartJMXException e) {
						if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process("IJMXEnabled[class="+ jmxComponent.getClass().getName() + ",name="+ jmxComponent.toString()+ "]: can't start it's JMX.", e));
					}
				}
				if (log.isLoggable(Level.INFO)) log.info(numOk + " JMX components enabled");
			}
		}
		return Pogamut.getPlatform().getMBeanServerURL().toString() + AgentJMXComponents.JMX_SERVER_AGENT_NAME_DELIM + getAgentJMXName().toString();
	}

	/**
	 * Factory method for creating agent MBean.
	 * 
	 * @param objectName
	 * @param mbs
	 * @return
	 */
	protected AgentMBeanAdapter createAgentMBean(ObjectName objectName,
			MBeanServer mbs) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException {
		return new AgentMBeanAdapter(agent, objectName, mbs);
	}
	
	/**
	 * Unregister all agent's MBeans / Listeners from MBeanServer.
	 */
	public void unregisterJMX() {
		if (isJMXEnabled()) {
			if (log.isLoggable(Level.WARNING)) log.warning("Unregistering JMX components.");
			mBeanServer.unregisterAll();
		}
	}
	
	/**
	 * Re-register all agent's MBeans / Listener into the MBeanServer again.
	 */
	public void registerJMX() {
		if (isJMXEnabled()) {
			try {
				if (log.isLoggable(Level.WARNING)) log.warning("Re-registering JMX components.");
				mBeanServer.registerAll();
			} catch (Exception e) {
				throw new CantStartJMXException("JMX components can't be re-registered: " + e.getMessage(), e, this);
			}
		}
	}
	
}
