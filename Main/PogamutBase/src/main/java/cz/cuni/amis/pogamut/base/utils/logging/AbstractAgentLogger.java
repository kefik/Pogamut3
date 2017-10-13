package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * All logging apis are fine ... but we don't want to have 
 * loggers for classes but for instances - therefore we've created
 * our wrapper allowing you to do two things quickly:
 * <ol>
 * <li>log things</li>
 * <li>create new logger categories</li>
 * </ol>
 * 1) that's obvious - it should be easy
 * <p>
 * 2) this may prove crucial for your debugging to have own logger
 * for planner and another for emotions of your agents, etc.
 * <p><p>
 * Simply - every Agent instance (starting with the first abstract class
 * AbstractAgent) has instance of this class (which is java.logging.Logger(s) wrapper).
 * <p>
 * Every agent's component has own {@link LogCategory} and you may obtain your own via getCategory() method.
 * <p><p>
 * {@link LogCategory} serves as a gateway for your log messages, it contains methods as you
 * know them from java.logging API (things like fine(), info(), severe(), log(Level, msg), etc.).
 * <p><p>
 * Plus it allows you to obtain new {@link LogHandler} instances for that category (if you need to 
 * publish log messages from that category somewhere else).
 * <p>
 * Every {@link LogHandler} serves for filtering messages for one category and publishing them
 * into one end (console, file, memory, whatever...).
 * <p><p>
 * Additionally every {@link LogCategory} has {@link AgentLogger} as its parent.
 * 
 * @author Jimmy
 */
@AgentScoped
public abstract class AbstractAgentLogger implements IAgentLogger {

	public static final String LOG_CATEGORY_NAME = "AgentLogger";

	protected IAgentId agentId;

	private List<Handler> defaultHandlers = new ArrayList<Handler>(4);
	
	private Level globalLevel = Level.INFO;
	
	private Handler consoleHandler = null;
	
	private Handler networkHandler = null;

	@Inject
	public AbstractAgentLogger(IAgentId agentName) {
		this.agentId = agentName;
	}

	@Override
	public IAgentId getAgentId() {
		return agentId;
	}
	
	@Override
	public Integer getNetworkLoggerPort() {
		return NetworkLogManager.getNetworkLogManager().getLoggerPort();
	}
	
	@Override
	public String getNetworkLoggerHost() {
		return NetworkLogManager.getNetworkLogManager().getLoggerHost();
	}
	
	@Override
	public Map<String, LogCategory> getCategories() {
		return getLogCategories().getCategories();
	}
	
	protected abstract ILogCategories getLogCategories();
	
    @Override
	public LogCategory getCategory(String name) {
    	if (getLogCategories().hasCategory(name)) return getLogCategories().getCategory(name);
		LogCategory category = getLogCategories().getCategory(name);
		synchronized(defaultHandlers) {
			for (Handler handler : defaultHandlers) {
				category.addHandler(handler);
			}
		}
		category.setLevel(globalLevel);
		return category;		
	}
    
    @Override
	public LogCategory getCategory(IComponent component) {
		return getCategory(component.getComponentId().getToken());
	}

    @Override
	public void setLevel(Level newLevel) {
    	globalLevel = newLevel;
    	getLogCategories().setLevel(newLevel);
	}
    
    @Override
    public synchronized void addDefaultConsoleHandler() {
    	if (consoleHandler != null) return;
    	consoleHandler = addDefaultPublisher(new LogPublisher.ConsolePublisher(getAgentId()));
    }
    
    @Override
    public Handler getDefaultConsoleHandler() {
    	return consoleHandler;
    }
    
    @Override
    public synchronized void removeDefaultConsoleHandler() {
    	if (consoleHandler == null) return;
    	removeDefaultHandler(consoleHandler);
    	consoleHandler.flush();
    	consoleHandler = null;
    }
    
    @Override
	public boolean isDefaultConsoleHandler() {
    	return consoleHandler != null;
    }
    
    @Override
    public synchronized void addDefaultNetworkHandler() {
    	if (networkHandler != null) return;
    	networkHandler = addDefaultPublisher(new NetworkLogPublisher(getAgentId()));
    }
    
    @Override
    public Handler getDefaultNetworkHandler() {
    	return networkHandler;
    }
    
    public synchronized void removeDefaultNetworkHandler() {
    	if (networkHandler == null) return;
    	removeDefaultHandler(networkHandler);
    	networkHandler.flush();
    	networkHandler = null;
    	NetworkLogManager.getNetworkLogManager().removeAgent(getAgentId());
    }
    
    @Override
	public boolean isDefaultNetworkHandler() {
    	return networkHandler != null;
    }
	
    @Override
	public Handler addDefaultFileHandler(File file) {
    	return addDefaultPublisher(new LogPublisher.FilePublisher(file, new LogFormatter(getAgentId(), true)));
    }
    
	@Override
	public Handler addDefaultPublisher(ILogPublisher publisher) {
		Handler defaultHandler = new LogHandler(publisher);
		addDefaultHandler(defaultHandler);
		return defaultHandler;
	}
	
	@Override
	public void addDefaultHandler(Handler handler) {
		synchronized(getCategories()) {
			synchronized(defaultHandlers) {
				defaultHandlers.add(handler);
			}
			addToAllCategories(handler);
		}
	}
	
	@Override
	public void removeDefaultHandler(Handler handler) {
		synchronized(getCategories()) {
			synchronized(defaultHandlers) {
				if (!defaultHandlers.remove(handler)) {
					// it is not a default handler
					return;
				}
			}
			removeFromAllCategories(handler);
		}
	}

	@Override
	public void addToAllCategories(ILogPublisher logPublisher) {
		synchronized(getCategories()) {
			for (LogCategory category : getCategories().values()) {
				category.addHandler(logPublisher);
			}
		}
	}

	@Override
	public void addToAllCategories(Handler handler) {
		synchronized(getCategories()) {
			for (LogCategory category : getCategories().values()) {
				category.addHandler(handler);
			}
		}
	}
	
	@Override
	public void removeFromAllCategories(Handler handler) {
		synchronized(getCategories()) {
			for (LogCategory category : getCategories().values()) {
				category.removeHandler(handler);
			}
		}
	}

}
