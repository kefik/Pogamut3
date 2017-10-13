package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.IJMXEnabled;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;

/**
 * Agent module that provides JMX interface. Implement {@link JMXAgentModule#enableJMX(MBeanServer, ObjectName)} to publish
 * the interface.
 * <p><p>
 * This module automatically registers itself into {@link AbstractAgent#getJMX()}.
 * 
 * @author Jimmy
 *
 * @param <AGENT>
 * 
 * @see AgentJMXComponents
 * @see IJMXEnabled
 */
public abstract class JMXAgentModule<AGENT extends AbstractAgent> extends AgentModule<AGENT> implements IJMXEnabled {
	
	/**
	 * Initialize agent module - it will start {@link ComponentDependencyType}.STARTS_WITH the agent.
	 * @param agent
	 */
	public JMXAgentModule(AGENT agent) {
		this(agent, null);
	}
	
	/**
	 * Initialize agent module - it will start {@link ComponentDependencyType}.STARTS_WITH the agent.
	 * @param agent
	 * @param log should be used, if <i>null</i> is provided, it is created automatically
	 */
	public JMXAgentModule(AGENT agent, Logger log) {
		super(agent, log);
		agent.getJMX().addComponent(this);
	}

	/**
	 * Register JMX components.
	 */
	@Override
	public abstract void enableJMX(MBeanServer mBeanServer, ObjectName parent) throws JMXAlreadyEnabledException, CantStartJMXException;

}
