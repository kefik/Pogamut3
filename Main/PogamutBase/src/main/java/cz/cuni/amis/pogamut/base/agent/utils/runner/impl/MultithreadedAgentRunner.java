package cz.cuni.amis.pogamut.base.agent.utils.runner.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * <b>BASIC USAGE</b>
 * <p><p>
 * Class used for starting the agent with certain default parameters. This class can't be used alone
 * as it is abstract. That's because the GaviaLib does not provide any concrete agent implementation
 * you may instance. Instead, the GaviaLib must be used together with some environment-Pogamut bridge
 * that defines a concrete {@link IAgent} with concrete {@link IAgentParameters}.
 * <p><p>
 * The class provides start-synchronization behavior of respective agents via {@link MultithreadedAgentRunner#setPausing(boolean)}.
 * For more information see {@link IAgentRunner#setPausing(boolean)}. 
 * <p><p>
 * Note that the class also provides you with hook-methods that can be utilized to additionally configure
 * agent instances as they are created and started. These are {@link MultithreadedAgentRunner#preInitHook()}, {@link MultithreadedAgentRunner#preStartHook(IAgent)},
 * {@link MultithreadedAgentRunner#preResumeHook(IAgent[])}, {@link MultithreadedAgentRunner#postStartHook(IAgent)} and {@link MultithreadedAgentRunner#postStartedHook(IAgent[])}.
 * <p><p>
 * This class is (almost complete) implementation that can instantiate and start one or multiple agents (of the same
 * class). The only thing that is left to be implemented is {@link MultithreadedAgentRunner#newDefaultAgentParameters()} that
 * are used to {@link IAgentParameters#assignDefaults(IAgentParameters)} into user provided parameters (if any of
 * are provided).
 * <p><p>
 * Additional features:
 * <ul>
 * <li>{@link MultithreadedAgentRunner#setLogLevel(Level)} - allows you to set default logging level for the newly created agent (default {@link Level#WARNING})</li>
 * <li>{@link MultithreadedAgentRunner#setConsoleLogging(boolean)} - allows you to attach default console logger (via {@link IAgentLogger#addDefaultConsoleHandler()})</li>
 * </ul>
 * <p><p>
 * This runner is based on the {@link IAgentFactory} interface that is utilized to create new instances. It is advised
 * that concrete agent runners hides this fact from the user instantiating the factory themselves so the user
 * must not dive deep into GaviaLib architecture.
 * <p><p>
 * <b>USING {@link MultithreadedAgentRunner} FROM THE MAIN METHOD</b>
 * <p><p>
 * There is one problem using Pogamut agents that comes from the decision to use JMX for the agents management.
 * If you start the agent in the main method and terminates it at some point in time - the JVM will not exit. That's
 * because of the rmi registry daemon thread that will hang up in the air preventing JVM from termination. This can
 * be handled by calling {@link PogamutPlatform#close()} in the end, that shuts down the rmi registry.
 * <p><p>
 * If you happen to need to use {@link MultithreadedAgentRunner} from the main method, do not forget to call {@link MultithreadedAgentRunner#setMain(boolean)} with param 'true'.
 * 
 * @author Jimmy
 * @author Aldarrion
 */
public abstract class MultithreadedAgentRunner<AGENT extends IAgent, PARAMS extends IAgentParameters> implements IAgentRunner<AGENT, PARAMS> {

	/**
	 * Used to uniquely number all agents across all {@link MultithreadedAgentRunner} instances.
	 */
	private static long ID = 0;
	
	/**
	 * Mutex we're synchronizing on when accessing {@link MultithreadedAgentRunner#ID}.
	 */
	private static Object idMutex = new Object();
	
	/**
	 * Mutex that synchronize killing of agents due to a failure.
	 */
	protected Object mutex = new Object();
	
	/**
	 * Used to instantiate new agents.
	 */
    protected IAgentFactory<AGENT, PARAMS> factory;
    
    /**
     * Use to log stuff.
     * <p><p>
     * WARNING: may be null! Always check it before logging!
     */
	protected Logger log;
	
	/**
	 * Whether the pausing feature is enables, see {@link MultithreadedAgentRunner#setPausing(boolean)}.
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
     * Whether we're currently killing all agents...
     */
    protected boolean killingAgents = false;
    
    /**
     * Guards {@link MultithreadedAgentRunner#killingAgents} access.
     */
    protected Object killingAgentsMutex = new Object();
    
    protected int cpuThreadCount = Runtime.getRuntime().availableProcessors();
    
	/**
	 * Listener that lowers the count on the {@link MainAgentRunner#latch} (if reaches zero, start method resumes and closes the Pogamut platform),
	 * and watches for the agent's failure ({@link MainAgentRunner#killAgents(IAgent[])} in this case).
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
    
    /**
	 * The runner needs the 'factory' so it know how to construct (instantiate) new agents.
	 * 
	 * @param factory preconfigured factory that is going to be used to produce new instances of agents
	 */
    public MultithreadedAgentRunner(IAgentFactory<AGENT, PARAMS> factory) {    	
        this.factory = factory;
        NullCheck.check(this.factory, "factory");
    }
    
    public Logger getLog() {
		return log;
	}

	public MultithreadedAgentRunner<AGENT, PARAMS> setLog(Logger log) {
		this.log = log;
		return this;
	}  
    
    // ----------------------
    // INTERFACE IAgentRunner
    // ----------------------
  
	@Override
    public synchronized AGENT startAgent() throws PogamutException {
		List<AGENT> agent;
		if (main) {
			agent = startAgentWithParamsMain(false, (PARAMS) newDefaultAgentParameters());
		} else {
			agent = startAgentWithParams(false, (PARAMS) newDefaultAgentParameters());
		}
        return agent.get(0);
    }
    
	// TODO keep blocking 
    @Override
    public synchronized List<AGENT> startAgents(int count) throws PogamutException {
    	PARAMS[] params = (PARAMS[]) new IAgentParameters[count];
    	for (int i = 0; i < params.length; ++i) params[i] = (PARAMS) newDefaultAgentParameters();
    	if (main) {
			return startAgentWithParamsMain(false, params);
		} else {
			return startAgentWithParams(false, params);
		}
    }
    
    @Override
	public synchronized List<AGENT> startAgents(PARAMS... agentParameters) throws PogamutException {
    	if (main) {
    		return startAgentWithParamsMain(true, agentParameters);
    	} else {
    		return startAgentWithParams(true, agentParameters);
    	}
	}
    
    @Override
    public boolean isPausing() {
    	return pausing;
    }
    
    @Override
    public synchronized MultithreadedAgentRunner<AGENT, PARAMS> setPausing(boolean state) {
    	this.pausing = state;
    	return this;
    }
    
    @Override
    public boolean isMain() {
    	return main;
    }
    
    @Override
    public synchronized MultithreadedAgentRunner<AGENT, PARAMS> setMain(boolean state) {
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
    public MultithreadedAgentRunner<AGENT, PARAMS> setLogLevel(Level logLevel) {
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
    public MultithreadedAgentRunner<AGENT, PARAMS> setConsoleLogging(boolean enabled) {
    	this.consoleLogging = enabled;
    	return this;
    }
    
    // --------------
    // IMPLEMENTATION
    // --------------
    
    /**
     * This method should be internally used to create and start the batch of agent instances.
     * <p><p>
     * 
     * @param fillDefaults whether the method should fill default values into the 'params'
     * @param params
     * @return
     */
    protected List<AGENT> startAgentWithParams(final boolean fillDefaults, final PARAMS... params) {
    	if (params == null || params.length == 0) return new ArrayList<AGENT>(0);
    	final List<AGENT> result = new ArrayList<AGENT>(params.length);
    	
    	final boolean pausingBehavior = isPausing();
    	
    	try {
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preInitHook()...");
	    	preInitHook();
	    	
	    	// Start first agent separately to avoid running twice heavy methods such as navigation loading
	    	startAndAddAgent(params[0], pausingBehavior, result);
	    	
	    	ExecutorService executor = Executors.newFixedThreadPool(cpuThreadCount);
	    	// Start rest of the agents if any
	    	for (int i = 0; i < params.length; ++i) {
	    	    if (fillDefaults) {
	                params[i].assignDefaults(newDefaultAgentParameters());
	            }
	    	    
	    	    final int iLoc = i;
	    	    executor.submit(new Runnable() {
					@Override
					public void run() {
						startAndAddAgent(params[iLoc], pausingBehavior, result);
					}	    	    	    	    	
	    	    });
	    	}
	    	
	    	executor.shutdown();
    	    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	    	
	    	if (pausingBehavior) {
	    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preResumeHook()...");
	    		preResumeHook(result);
	    		for (AGENT agent : result) {
	    			agent.resume();
	    		} 
	    	}
	    		
	    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartedHookCalled()...");
	    	postStartedHook(result);
    	} catch (PogamutException e) {
    		killAgents(result);
    		throw e;
    	} catch (Exception e) {
    		killAgents(result);
    		throw new PogamutException("Agent's can't be started: " + e.getMessage(), e, this);
    	}
    	
    	return result;
    }
    
    /**
     * E.g. {@link MultithreadedAgentRunner#startAgentWithParams(boolean, IAgentParameters[])} but 
     * provides the blocking mechanism.
	 */
	protected List<AGENT> startAgentWithParamsMain(boolean fillDefaults, final PARAMS... params) {
		if (params == null || params.length == 0) return new ArrayList<AGENT>(0);
		latch = new CountDownLatch(params.length);
		
		agents = new ArrayList<AGENT>(params.length);
    	killed = false;
		
		final boolean pausingBehavior = isPausing();
    	
    	try {
    	    // Start first agent separately to avoid running twice heavy methods such as navigation loading
	    	startAndAddAgentMain(params[0], pausingBehavior);
    	    
	    	ExecutorService executor = Executors.newFixedThreadPool(cpuThreadCount);
	    	// Start rest of the agents if any
	    	for (int i = 1; i < params.length; ++i) {	    		
	    		if (killed) break;
	    		
	    		if (fillDefaults) {
	    			params[i].assignDefaults(newDefaultAgentParameters());
	    		}
	    		
	    		final int iLoc = i;
	    		executor.submit(new Runnable() {

					@Override
					public void run() {
						startAndAddAgentMain(params[iLoc], pausingBehavior);
					}
	    			
	    		});
	    	}
	    	
	    	executor.shutdown();
	    	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	    	
	    	if (!killed) {
		    	if (pausingBehavior) {
		    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preResumeHook()...");
		    		preResumeHook(agents);
		    		for (AGENT agent : agents) {
		    			agent.resume();
		    		} 
		    	}
		    	if (!killed) {	
		    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartedHookCalled()...");
		    		postStartedHook(agents);
		    	}
		    	
		    	if (!killed) {
		    		try {
		    			latch.await();
		    		} catch (InterruptedException e) {
		    			throw new PogamutInterruptedException("Interrupted while waiting for the agents to finish their execution.", e, this);
		    		}
		    	}
	    	}
	    	
	    	if (killed) {
				throw new PogamutException("Could not execute all agents due to an exception, check logs of respective agents.", this);
			}
	    	
	    	return agents;
	    	
    	} catch (PogamutException e) {
    		killAgents(agents);
    		throw e;
    	} catch (Exception e) {
    		killAgents(agents);
    		throw new PogamutException("Agents can't be started: " + e.getMessage(), e, this);
    	} finally {   
    		Pogamut.getPlatform().close();
		}		
	};
    
    private void startAndAddAgentMain(PARAMS params, boolean pausingBehavior)
    {
        AGENT agent = createAgentWithParams(params);
        
        if (killed) return;
        
        if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preStartHook()...");
        preStartHook(agent);
        
        if (killed) return;
        
        startAgent(agent);
        
        if (killed) {
            killAgent(agent);
            return;
        }
        
        if (pausingBehavior) {
            agent.pause();
        }
        
        if (killed) {
            killAgent(agent);
            return;
        }
        
        if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartHook()...");
        postStartHook(agent);
        
        if (killed) {
            killAgent(agent);
            return;
        }
        
        synchronized(mutex) {
            if (killed) {
                killAgent(agent);
                return;
            }
            agents.add(agent);
        }
    }
    
    private void startAndAddAgent(PARAMS params, boolean pausingBehavior, List<AGENT> result)
	    {
            AGENT agent = createAgentWithParams(params);
            
            if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling preStartHook()...");
            preStartHook(agent);
            
            startAgent(agent);
            
            if (pausingBehavior) {
                agent.pause();
            }
            
            if (log != null && log.isLoggable(Level.FINE)) log.fine("Calling postStartHook()...");
            postStartHook(agent);
            
            synchronized(result) {
                result.add(agent);
            }
	    }
	
	
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
     * Creates new {@link AgentId} from the 'name' and unique number that is automatically generated
     * from the {@link MultithreadedAgentRunner#ID}.
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
     * Fills defaults parameters into the 'params' by using {@link MultithreadedAgentRunner#newDefaultAgentParameters()}.
     * @param params
     */
    protected void fillInDefaults(PARAMS params) {
    	params.assignDefaults(newDefaultAgentParameters());
    }
    
    /**
     * Fills defaults parameters into every 'params' of the array by using {@link MultithreadedAgentRunner#newDefaultAgentParameters()},
     * i.e., we're creating a new default parameters for every 'params' from the array.
     * @param params
     */
    protected void fillInDefaults(PARAMS[] paramsArray) {
    	for (PARAMS params : paramsArray) {
    		params.assignDefaults(newDefaultAgentParameters());
    	}
    }
    
    /**
     * Method that is used by {@link MultithreadedAgentRunner#startAgentWithParams(IAgentParameters[])} to instantiate new 
     * agents. Uses {@link MultithreadedAgentRunner#factory} to do the job. It assumes the params were already configured 
     * with defaults.
     * 
     * @param params
     * @return
     */
    protected AGENT createAgentWithParams(PARAMS params) {
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
     * Method that is used by {@link MultithreadedAgentRunner#startAgentWithParams(IAgentParameters[])} to start newly
     * created agent.
     * 
     * @param agent
     */
    protected void startAgent(AGENT agent) {
    	if (main) {
    		agent.getState().addListener(listener);
    	}
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Starting agent with id '" + agent.getComponentId().getToken() + "'");
    	agent.start();
    }
    
    /**
     * This method is called whenever start/pause/resume of the single agent fails to clean up.
     * <p><p>
     * Recalls {@link MultithreadedAgentRunner#killAgent(IAgent)} for every non-null agent instance.
     * 
     * @param agents some array elements may be null!
     */
    protected void killAgents(List<AGENT> agents) {    	
    	synchronized(killingAgentsMutex) {
    		if (killingAgents) return;
    		killingAgents = true;
    	}
    	// WE'RE ALONE HERE!
    	try {
	    	synchronized(mutex) {
	    		if (main) {
	    			if (killed) return;
	    			if (agents == null) return;
	    			while (latch.getCount() > 0) {
	    				latch.countDown();
	    			}
	    			killed = true;
	    		}
	    		if (agents == null) return;
	        	for (AGENT agent : agents) {
	        		if (agent != null) {
	        			killAgent(agent);
	        		}
	        	}
	    	}
    	} finally {
    		killingAgents = false;
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
     * Custom hook called before all the agents are going to be instantiated by the {@link MultithreadedAgentRunner#factory}.
     * <p><p>
     * May be utilized by the GaviaLib user to inject additional code into the runner.
     */
    protected void preInitHook() throws PogamutException {    	
    }

    /**
     * Custom hook called after the agent is instantiated by the {@link MultithreadedAgentRunner#factory} and before
     * the {@link IAgent#start()} is called.
     * <p><p>
     * May be utilized by the GaviaLib user to inject additional code into the runner.
     * 
     * @param agent
     */
    protected void preStartHook(AGENT agent) throws PogamutException {
    }
    
    /**
     * Custom hook called after the agent is instantiated by the {@link MultithreadedAgentRunner#factory} and
     * started with {@link IAgent#start()}.
     * <p><p>
     * May be utilized by the GaviaLib user to inject additional code into the runner.
     * 
     * @param agent
     * @throws PogamutException
     */
    protected void postStartHook(AGENT agent) throws PogamutException {    	
    }
    
    /**
     * Custom hook called only iff {@link MultithreadedAgentRunner#isPausing()}. This method is called after all the agents have been instantiated by the {@link MultithreadedAgentRunner#factory}
     * and before they are resumed by {@link IAgent#resume()}.
     * <p><p>
     * May be utilized by the GaviaLib user to inject additional code into the runner.
     * 
     * @param agents
     */
    protected void preResumeHook(List<AGENT> agents) {
    }
    
    /**
     * Custom hook called after all the agents have been instantiated by the {@link MultithreadedAgentRunner#factory}
     * and started with {@link IAgent#start()}.
     * <p><p>
     * May be utilized by the GaviaLib user to inject additional code into the runner.
     * 
     * @param agents
     */
    protected void postStartedHook(List<AGENT> agents) {
    }
    
}
