package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.File;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.jmx.IJMXEnabled;
import cz.cuni.amis.pogamut.base.component.IComponent;

/**
 * Basic interface for agent's logs.
 * @author ik
 */
public interface IAgentLogger extends IJMXAgentLogger, IJMXEnabled {
	
	/**
	 * Returns agent name.
	 * @return
	 */
	public IAgentId getAgentId();
	
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
	 * Returns LogCategory for specified {@link IComponent}.
	 * @param component
	 * @return
	 */
	public LogCategory getCategory(IComponent component);

	/**
	 * Returns LogCategory for specified name. If category with
	 * this name doesn't exist new is created.
	 * @param name
	 * @return
	 */
	public LogCategory getCategory(String name);

	/**
	 * Return immutable map of all log categories. You have to synchronize on it before iterating through its elements.
	 * <p><p>
	 * Does not contain agent logger itself.
	 * 
	 * @return
	 */
	public Map<String, LogCategory> getCategories();
	
	/**
	 * Adds console handler to every existing {@link LogCategory} plus to every new one.
	 * <p><p>
	 * Shortcut for quick usage.
	 */
	public void addDefaultConsoleHandler();
	
	/**
	 * Returns {@link Handler} that provides console publishing of all logs.
	 * <p><p>
	 * May return null in case that console logging is not enabled on the logger, i.e., you have to call 
	 * {@link IAgentLogger#addDefaultConsoleHandler()}.
	 * 
	 * @return
	 */
	public Handler getDefaultConsoleHandler();
	
	/**
	 * Removes default console handler from every existing {@link LogCategory}.
	 */
	public void removeDefaultConsoleHandler();
	
	/**
	 * Tells whether the logger has default console handler attached.
	 * @return
	 */
	public boolean isDefaultConsoleHandler();
	
	/**
	 * Adds network handler to every existing {@link LogCategory} plus to every new one.
	 * <p><p>
	 * Enables utilization of {@link NetworkLogManager} for publishing all logs of this logger.
	 */
	public void addDefaultNetworkHandler();
	
	/**
	 * Returns {@link Handler} that provides publishing of all logs through {@link NetworkLogPublisher}.
	 * <p><p>
	 * May return null in case that network logging is not enabled on the logger, i.e., you have to call 
	 * {@link IAgentLogger#addDefaultNetworkHandler()}.
	 * 
	 * @return
	 */
	public Handler getDefaultNetworkHandler();
	
	/**
	 * Removes default network handler from every existing {@link LogCategory}.
	 * <p><p>
	 * Note that this method is automatically called whenever the AbstractAgent is stopped/killed.
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
	
	/**
	 * Adds console handler to every existing {@link LogCategory} plus to every new one.
	 * <p><p>
	 * Shortcut for quick usage.
	 * 
	 * @return new added handler
	 */
	public Handler addDefaultFileHandler(File file);
	
	/**
	 * Adds publisher to every existing {@link LogCategory} plus to every new one.
	 * @param publisher
	 * @return newly added handler
	 */
	public Handler addDefaultPublisher(ILogPublisher publisher);
	
	/**
	 * Adds handler to every existing {@link LogCategory} plus to every new one.
	 * @param handler
	 */
	public void addDefaultHandler(Handler handler);
	
	/**
	 * Removes default handler from all existing {@link LogCategory}.
	 * 
	 * @param handler
	 */
	public void removeDefaultHandler(Handler handler);

	/**
	 * Adds new publisher to all categories.
	 * @param logPublisher
	 */
	public void addToAllCategories(ILogPublisher logPublisher);

	/**
	 * Adds new handler to all categories.
	 * @param handler
	 */
	public void addToAllCategories(Handler handler);
	
	/**
	 * Removes a handler from all categories.
	 * @param handler
	 */
	public void removeFromAllCategories(Handler handler);

	/**
	 * Set level for all handlers of all categories.
	 * @param newLevel
	 */
	public void setLevel(Level newLevel);
	
}
