package cz.cuni.amis.pogamut.base.agent.utils.runner.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentDescriptor;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Class used for starting the agent with certain default parameters. 
 * <p><p>
 * The class is similar to the {@link MultipleAgentRunner} but implements different interface ({@link IMultipleAgentRunner}),
 * that is, it allows to start different classes of agents at once. In fact it uses {@link MultipleAgentRunner} to do the job.
 * <p><p>
 * 
 * TODO!!!
 * 
 * Additional features:
 * <ul>
 * <li>{@link AgentRunner#setLogLevel(Level)} - allows you to set default logging level for the newly created agent (default {@link Level#WARNING})</li>
 * <li>{@link AgentRunner#setConsoleLogging(boolean)} - allows you to attach default console logger (via {@link IAgentLogger#addDefaultConsoleHandler()})</li>
 * </ul>

 * 
 * The class provides start-synchronization behavior of respective agents via {@link MultipleAgentRunner#setPausing(boolean)}.
 * For more information see {@link IAgentRunner#setPausing(boolean)}. 
 * <p><p>
 * Note that the class also provides you with hook-methods that can be utilized to additionally configure
 * agent instances as they are created and started. These are {@link MultipleAgentRunner#preInitHook()}, {@link MultipleAgentRunner#preStartHook(IAgent)},
 * {@link MultipleAgentRunner#preResumeHook(IAgent[])}, {@link MultipleAgentRunner#postStartHook(IAgent)} and {@link MultipleAgentRunner#postStartedHook(IAgent[])}.
 * <p><p>
 * This class is (almost complete) implementation that can instantiate and start one or multiple agents (of the same
 * class). The only thing that is left to be implemented is {@link MultipleAgentRunner#newDefaultAgentParameters()} that
 * are used to {@link IAgentParameters#assignDefaults(IAgentParameters)} into user provided parameters (if any of
 * are provided).
 * <p><p>
 * This runner is based on the {@link IAgentFactory} interface that is utilized to create new instances. It is advised
 * that concrete agent runners hides this fact from the user instantiating the factory themselves so the user
 * must not dive deep into GaviaLib architecture.
 * 
 * @author Jimmy
 */
public abstract class MultipleAgentRunner<AGENT extends IAgent, PARAMS extends IAgentParameters, MODULE extends GuiceAgentModule> implements IMultipleAgentRunner<AGENT, PARAMS, MODULE> {
    
	/**
	 * Used to uniquely number all agents across all {@link MultipleAgentRunner} instances.
	 */
	private static long ID = 0;
	
	/**
	 * Mutex we're synchronizing on when accessing {@link MultipleAgentRunner#ID}.
	 */
	private static Object idMutex = new Object();
	
	/**
	 * Mutex that synchronize killing of agents due to a failure.
	 */
	protected Object mutex = new Object();
	
    /**
     * Use to log stuff.
     * <p><p>
     * WARNING: may be null! Always check it before logging!
     */
	protected Logger log;
	
	/**
	 * Whether the pausing feature is enables, see {@link AgentRunner#setPausing(boolean)}.
	 */
	private boolean pausing = false;
	
	/**
	 * Default log level that is set to the agent after its instantiation.
	 */
	protected Level defaultLogLevel = Level.WARNING;
	
	/**
	 * Whether the console logging is enabled as default. (Default is TRUE.)
	 */
	protected boolean consoleLogging = true;
	
	////
	// FIELDS USED MAINLY FOR 'setMain(true)' FUNCTIONALITY
	////
	
	/**
     * Latch where we're awaiting till all agents finishes.
     */
    protected CountDownLatch latch;
    
    /**
     * List of started agents.
     */
    protected List<AGENT> agents = null;
    
    /**
     * Whether we had to kill all agents (i.e., some exception/failure has happened).
     */
    protected boolean killed = false;
    
    /**
     * Whether we should provide 'main' feature.
     */
    protected boolean main = false;
    
	/**
	 * Listener that lowers the count on the {@link MultipleAgentRunner#latch} (if reaches zero, start method resumes and closes the Pogamut platform),
	 * and watches for the agent's failure ({@link MultipleAgentRunner#killAgents(IAgent[])} in this case).
	 */
	protected FlagListener<IAgentState> listener = new FlagListener<IAgentState>() {
		@Override
		public void flagChanged(IAgentState changedValue) {
			if (changedValue instanceof IAgentStateFailed) {
				killAgents(agents);
			} else
			if (changedValue instanceof IAgentStateDown) {
				latch.countDown();
			}
		}    	        	
    };   

	public MultipleAgentRunner<AGENT, PARAMS, MODULE> setLog(Logger log) {
		this.log = log;
		return this;
	}
	
	public Logger getLog() {
		return log;
	}

	/**
     * Sums all {@link IAgentDescriptor#getCount()}.
     * 
     * @param agentDescriptors
     * 
     * @return number of agents described by 'agentDescriptors'
     */
    public int getAgentCount(IAgentDescriptor<PARAMS,MODULE>... agentDescriptors) {
    	int result = 0;
    	for (IAgentDescriptor<PARAMS, MODULE> descriptor : agentDescriptors) {
    		result += descriptor.getCount();
    	}
    	return result;
    }
    
    // ---------
    // ABSTRACTS
    // ---------
    
    /**
     * Method that is called to provide another default parameters for newly created agents.
     * <p><p>
     * Note that it might be the case, that some parameters can't be shared between agent instances,
     * thus you might always need to provide a new parameters. This decision is up to you as an implementor
     * (that means you must understand how these parameters are used by the particular agent, fail-safe behaviour
     * is to always provide a new one).
     * <p><p>
     * Notice that the method does not require you to provide parameters of the type 'PARAMS' allowing you
     * to provide any params as defaults.
     * 
     * @return new default parameters
     */
    protected abstract IAgentParameters newDefaultAgentParameters();
    
    /**
     * Creates a new factory for the given 'agentModule'. Called from within {@link MultipleAgentRunner#startAgents(IAgentDescriptor, List)}
     * to obtain a factory for the {@link IAgentDescriptor#getAgentModule()}.
     * 
     * @param module
     * @return new factory configured with 'agentModule'
     */
    protected abstract IAgentFactory newAgentFactory(MODULE agentModule);
    
    // ------------------------------
    // INTERFACE IMultipleAgentRunner
    // ------------------------------
    
    public synchronized List<AGENT> startAgents(IAgentDescriptor<PARAMS,MODULE>... agentDescriptors) {
    	if (main) {
    		return startAgentsMain(agentDescriptors);
    	} else {
    		return startAgentsStandard(agentDescriptors);
    	}
    }
    
    @Override
    public boolean isPausing() {
    	return pausing;
    }
    
    @Override
    public synchronized MultipleAgentRunner<AGENT, PARAMS, MODULE> setPausing(boolean state) {
    	this.pausing = state;
    	return this;
    }
    
    @Override
    public boolean isMain() {
    	return main;
    }
    
    @Override
    public synchronized MultipleAgentRunner<AGENT, PARAMS, MODULE> setMain(boolean state) {
    	this.main = state;
    	return this;
    }
    
    // --------------------------
    // ADDITIONAL UTILITY METHODS
    // --------------------------
    
    /**
     * Sets default logging level for newly created agents (default is {@link Level#WARNING}).
     * <p><p>
     * If set to null, no level is set as default to the agent logger.
     * <p><p>
     * This probably violates the way how logging should be set up, but is more transparent for beginners.
     * 
     * @return this instance
     */
    public MultipleAgentRunner<AGENT, PARAMS, MODULE> setLogLevel(Level logLevel) {
    	this.defaultLogLevel = logLevel;
    	return this;
    }
    
    /**
     * Allows you to disable/enable default console logging for the agent. (Default is TRUE.)
     * <p><p>
     * This probably violates the way how logging should be set up, but is more transparent for beginners.
     * 
     * @param enabled
     * @return
     */
    public MultipleAgentRunner<AGENT, PARAMS, MODULE> setConsoleLogging(boolean enabled) {
    	this.consoleLogging = enabled;
    	return this;
    }
    
    // --------------
    // IMPLEMENTATION
    // --------------
    
    protected List<AGENT> startAgentsStandard(IAgentDescriptor<PARAMS,MODULE>... agentDescriptors) {
    	int count = getAgentCount(agentDescriptors);
    	if (count == 0) return new ArrayList<AGENT>(0);
    	
    	List<AGENT> result = new ArrayList<AGENT>(count);    	
    	
    	try {
    	
	    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preInitHook()...");
	    	preInitHook();
	    	
	    	for (IAgentDescriptor<PARAMS,MODULE> descriptor : agentDescriptors) {
	    		startAgentsStandard(descriptor, result);
	    	}
	    	
	    	if (isPausing()) {
	    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preResumeHook()...");
	    		preResumeHook(result);
	    		for (AGENT agent : result) {
	    			agent.resume();
	    		} 
	    	}
	    	
	    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartedHookCalled()...");
	    	postStartedHook(result);
	    	
	    	return result;
	    	
    	}  catch (PogamutException e) {
    		killAgents(result);
    		throw e;
    	} catch (Exception e) {
    		killAgents(result);
    		throw new PogamutException("Agent's can't be started: " + e.getMessage(), e, this);
    	}
    };
    
	protected synchronized List<AGENT> startAgentsMain(IAgentDescriptor<PARAMS,MODULE>... agentDescriptors) {
    	int count = getAgentCount(agentDescriptors);
    	if (count == 0) return new ArrayList<AGENT>(0);
    	
    	agents = new ArrayList<AGENT>(count);
    	latch = new CountDownLatch(count);
    	killed = false;
    	
    	boolean pausingBehavior = isPausing();
    	
    	//try {
    	
	    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preInitHook()...");
	    	preInitHook();
	    	
	    	for (IAgentDescriptor<PARAMS,MODULE> descriptor : agentDescriptors) {
	    		if (killed) break;
	    		startAgentsMain(descriptor, pausingBehavior, agents);
	    	}
	    	
	    	if (pausingBehavior) {
	    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preResumeHook()...");
	    		preResumeHook(agents);
	    		for (AGENT agent : agents) {
	    			if (killed) break;
	    			agent.resume();	    			
	    		} 
	    	}
	    	
	    	if (!killed) {
		    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartedHookCalled()...");
		    	postStartedHook(agents);
		    	
		    	try {
					latch.await();
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException("Interrupted while waiting for the agents to finish their execution.", e, this);
				}
	    	}
	    	
	    	if (killed) {
	    		throw new PogamutException("Could not execute all agents due to an exception, see logs of respective agents.", this);
	    	}
			
	    	return agents;
	    	
//    	}  catch (PogamutException e) {
//    		killAgents(agents);
//    		throw e;
//    	} catch (Exception e) {
//    		killAgents(agents);
//    		throw new PogamutException("Agent's can't be started: " + e.getMessage(), e, this);
//    	} finally {
//    		Pogamut.getPlatform().close();    		
//    	}
    }
    
    /**
     * Starts all agents described by 'agentDescriptor', puts new agent instances into 'result'.
     * <p><p>
     * Does not catch exceptions (they are propagated by JVM as usual).
     * 
     * @param agentDescriptor
     * @param result
     */
    protected void startAgentsStandard(IAgentDescriptor<PARAMS,MODULE> agentDescriptor, List<AGENT> result) {
    	if (agentDescriptor == null || agentDescriptor.getCount() == 0) return;
    	
    	IAgentFactory<AGENT, PARAMS> agentFactory = newAgentFactory(agentDescriptor.getAgentModule());
    	
    	PARAMS[] agentParams = agentDescriptor.getAgentParameters();
    	
    	for (int i = 0; i < agentDescriptor.getCount(); ++i) {
    		PARAMS params = null;
    		if (agentParams.length > i) {
    			params = agentParams[i];
    			params.assignDefaults(newDefaultAgentParameters());
    		} else {
    			params = (PARAMS) newDefaultAgentParameters();
    		}
    		
    		AGENT agent = createAgentWithParams(agentFactory, params);
    		
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preStartHook()...");
    		preStartHook(agent);
    		
    		startAgent(agent);
    		
    		if (isPausing()) {
    			agent.pause();
    		}
    		
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartHook()...");
    		postStartHook(agent);
    		
    		result.add(agent);
    	}
    }    
    
    /**
	 * Overridden to provide the blocking mechanism. (For more info see {@link AgentRunner#startAgentWithParams(boolean, IAgentParameters...)}.
	 * <p><p>
	 */
	protected void startAgentsMain(IAgentDescriptor<PARAMS,MODULE> agentDescriptor, boolean pausingBehavior, List<AGENT> result) {
    	if (agentDescriptor == null || agentDescriptor.getCount() == 0) return;
    	
    	if (killed) return;
    	
    	IAgentFactory<AGENT, PARAMS> agentFactory = newAgentFactory(agentDescriptor.getAgentModule());
    	
    	PARAMS[] agentParams = agentDescriptor.getAgentParameters();
    	
    	for (int i = 0; i < agentDescriptor.getCount(); ++i) {
    		PARAMS params = null;
    		if (agentParams.length > i) {
    			params = agentParams[i];
    			params.assignDefaults(newDefaultAgentParameters());
    		} else {
    			params = (PARAMS) newDefaultAgentParameters();
    		}
    		
    		if (killed) return;
    		
    		AGENT agent = createAgentWithParams(agentFactory, params);
    		result.add(agent);
    		
    		if (killed) return;
    		
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preStartHook()...");
    		preStartHook(agent);
    		
    		if (killed) return;
    		
    		startAgent(agent);
    		
    		if (pausingBehavior) {
    			if (killed) return;
    			agent.pause();
    		}
    		
    		if (killed) return;
    		
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartHook()...");
    		postStartHook(agent);
    	}
    }
    
    /**
     * Creates new {@link AgentId} from the 'name' and unique number that is automatically generated
     * from the {@link MultipleAgentRunner#ID}.
     * 
     * @param name
     */
    protected IAgentId newAgentId(String name) {
    	if (name == null) name = "Unnamed";
    	synchronized(idMutex) {
    		return new AgentId(name + (++ID));
    	}
    }
    
    /**
     * Method that is used by {@link MultipleAgentRunner#startAgentWithParams(IAgentParameters[])} to instantiate new 
     * agents. Uses {@link MultipleAgentRunner#factory} to do the job. It assumes the params were already configured 
     * with defaults.
     * 
     * @param params
     * @return
     */
    protected AGENT createAgentWithParams(IAgentFactory<AGENT, PARAMS> factory, PARAMS params) {
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Instantiating agent with id '" + params.getAgentId().getToken() + "'");
    	AGENT agent = factory.newAgent(params);
    	if (consoleLogging) {
    		agent.getLogger().addDefaultConsoleHandler();
    	}
    	if (defaultLogLevel != null) {
    		agent.getLogger().setLevel(defaultLogLevel);
    	}    	
    	return agent;
    }
    
    /**
     * Method that is used by {@link MultipleAgentRunner#startAgentWithParams(IAgentParameters[])} to start newly
     * created agent.
     * 
     * @param agent
     */
    protected void startAgent(AGENT agent) {
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Starting agent with id '" + agent.getComponentId().getToken() + "'");
    	if (main) {
    		agent.getState().addListener(listener);
    	}
    	agent.start();
    }
    
    /**
     * This method is called whenever start/pause/resume of the single agent fails to clean up.
     * <p><p>
     * Recalls {@link MultipleAgentRunner#killAgent(IAgent)} for every non-null agent instance.
     * 
     * @param agents some array elements may be null!
     */
    protected void killAgents(List<AGENT> agents) {
    	if (agents == null) return;
    	synchronized(mutex) {
			if (main) {
				if (killed) return;
				while (latch.getCount() > 0) {
					latch.countDown();
				}
				killed = true;
			}			
			for (AGENT agent : agents) {
	    		if (agent != null) {
	    			killAgent(agent);
	    		}
	    	}
		}
    	
    }
    
    /**
     * Kills a single agent instance, called during clean up when start/pause/resume of the agent fails.
     * @param agent
     */
    protected void killAgent(AGENT agent) {
    	if (agent == null) return;
    	synchronized(mutex) {
    		if (main) {
    			agent.getState().removeListener(listener);
    		}
    		if (!(agent.getState().getFlag() instanceof IAgentStateDown)) {
        		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Killing agent with id '" + agent.getComponentId().getToken() + "'");
        		try {
        			agent.kill();
        		} catch (Exception e) {    			
        		}
        	}
    	}    	
    }
	
    /**
     * Custom hook called before all the agents are going to be instantiated.
     */
    protected void preInitHook() throws PogamutException {    	
    }

    /**
     * Custom hook called after the agent is instantiated  and before
     * the {@link IAgent#start()} is called.
     * @param agent
     */
    protected void preStartHook(AGENT agent) throws PogamutException {
    }
    
    /**
     * Custom hook called after the agent is instantiated and
     * started with {@link IAgent#start()}.
     * @param agent
     * @throws PogamutException
     */
    protected void postStartHook(AGENT agent) throws PogamutException {    	
    }
    
    /**
     * Custom hook called only iff {@link MultipleAgentRunner#isPausing()}. 
     * This method is called after all the agents have been instantiated by the {@link MultipleAgentRunner#factory}
     * and resumed with {@link IAgent#resume()}.
     * @param agents
     */
    protected void preResumeHook(List<AGENT> agents) {
    }
    
    /**
     * Custom hook called after all the agents have been instantiated by the {@link MultipleAgentRunner#factory}
     * and started with {@link IAgent#start()}.
     * @param agents
     */
    protected void postStartedHook(List<AGENT> agents) {
    }

}
