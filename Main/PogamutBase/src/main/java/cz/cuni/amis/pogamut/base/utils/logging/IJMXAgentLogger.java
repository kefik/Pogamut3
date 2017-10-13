package cz.cuni.amis.pogamut.base.utils.logging;

import javax.management.MXBean;

/**
 * Part of the {@link IAgentLogger} interface that is exposed via MBean.
 * @author Jimmy
 */
@MXBean
public interface IJMXAgentLogger {
	
	/**
	 * Returns port where {@link NetworkLogManager} is listening.
	 * <p><p>
	 * Returns null if network logging is not enabled.
	 * 
	 * @return
	 */
	public Integer getNetworkLoggerPort();
	
	/**
	 * Returns a host where {@link NetworkLogManager} is listening.
	 * <p><p>
	 * Returns null if network logging is not enabled.
	 * 
	 * @return
	 */
	public String getNetworkLoggerHost();

	/**
	 * Adds network handler to every existing {@link LogCategory} plus to every new one.
	 * <p><p>
	 * Enables utilization of {@link NetworkLogManager} for publishing all logs of this logger.
	 */
	public void addDefaultNetworkHandler();
	
	/**
	 * Removes default network handler from every existing {@link LogCategory}.
	 */
	public void removeDefaultNetworkHandler();
	
	/**
	 * Tells whether the logger has default network handler attached. 
	 * <p><p>
	 * It allows you to query whether the agent logger is outputting its logs to the {@link NetworkLogManager}
	 * or not.
	 * 
	 * @return
	 */
	public boolean isDefaultNetworkHandler();
	
}
