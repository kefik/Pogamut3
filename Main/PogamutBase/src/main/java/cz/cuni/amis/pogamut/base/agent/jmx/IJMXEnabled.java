package cz.cuni.amis.pogamut.base.agent.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;

/**
 * Interface for enabling JMX on some feature.
 * <p><p>
 * This interface is marking that class supports JMX somehow and can be added to the agent who
 * will call this method whenever this method is called on the whole agent.
 * 
 * @author Jimmy
 */
public interface IJMXEnabled {
	
	/**
	 * Method for starting the JMX extension of the class. 
	 * <p><p>
	 * Object should register whatever objects it wants to expose via JMX.
	 * 
	 * @param mBeanServer server where the MBean of agent is registered
	 * @param parent parent's ObjectName, should be used as base of name of 
     * the registered MBean
     * @throws JMXAlreadyEnabledException
	 */
	public void enableJMX(MBeanServer mBeanServer, ObjectName parent) throws JMXAlreadyEnabledException, CantStartJMXException;
	
}
