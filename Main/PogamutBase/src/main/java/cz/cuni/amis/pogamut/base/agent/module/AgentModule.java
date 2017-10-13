package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Ancestor of all agent modules that contains {@link ComponentController} and defines protected methods for the control of
 * the module's lifecycle ({@link AgentModule#start()}, {@link AgentModule#stop()}, {@link AgentModule#pause()}, {@link AgentModule#resume()},
 * {@link AgentModule#kill()} and {@link AgentModule#reset()}). Override them when needed.
 * 
 * @author Jimmy
 *
 * @param <AGENT>
 */
public abstract class AgentModule<AGENT extends IAgent> implements IComponent {
	
	private static int moduleNumber = 0;
	private static Object moduleNumberMutex = new Object();
	
	protected final AGENT agent;
	protected Logger log;
	protected final ComponentController controller;
	protected final IComponentBus eventBus;
	
	private final Token componentId;
	
	/**
	 * Initialize agent module - it will start {@link ComponentDependencyType}.STARTS_WITH the agent.
	 * @param agent
	 */
	public AgentModule(AGENT agent) {
		this(agent, null);
	}
	
	/**
	 * Initialize agent module - it will start {@link ComponentDependencyType}.STARTS_WITH the agent.
	 * @param agent
	 * @param log should be used, if <i>null</i> is provided, it is created automatically
	 */
	public AgentModule(AGENT agent, Logger log) {
		this(agent, log, null);
	}
	
	/**
	 * Initialize agent module with custom dependencies.
	 * @param agent
	 * @param log which should be used, if <i>null</i> is provided, it is created automatically
	 * @param dependencies
	 */
	public AgentModule(AGENT agent, Logger log, ComponentDependencies dependencies) {
		this.agent = agent;
		NullCheck.check(agent, "agent");
		this.componentId = initComponentId();
		NullCheck.check(this.componentId, "componentId initialization");
		if (log == null) {
			this.log = agent.getLogger().getCategory(this);
		} else {
			this.log = log;
		}
		this.eventBus = agent.getEventBus();				
		if (dependencies == null) {
			dependencies = new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agent);
		}
		this.controller = new ComponentController(this, control, this.eventBus, this.log, dependencies);
		this.eventBus.register(this);
	}
	
	/**
	 * Returns a logger used by the AgentModule. AgentModule always has a logger, even if you do 
	 * not supply it with one (it will allocate one on its own).
	 * @return
	 */
	public Logger getLog() {
		return log;
	}
	
	/**
	 * Returns token made from simple name of the module's class.
	 * <p><p>
	 * Called during the construction of the module, called only once (and even before your constructors take effect)!
	 * 
	 * @return component id
	 */
	protected Token initComponentId() {
		synchronized(moduleNumberMutex) {
			return Tokens.get(this.getClass().getSimpleName() + (moduleNumber++));
		}
	}
	
	@Override
	public Token getComponentId() {
		return componentId;
	}
	
	/**
	 * Whether the component is running.
	 * @return
	 * @see ComponentController#isRunning()
	 */
	public boolean isRunning() {
		return controller.isRunning();
	}

	/**
	 * Returns state of the component.
	 * @return
	 */
	public ImmutableFlag<ComponentState> getState() {
		return controller.getState();
	}
	
	@Override
	public String toString() {
		if (this == null) return "AgentModule-instantiating";
		return getClass().getSimpleName();
	}

	private IComponentControlHelper control = new ComponentControlHelper() {
		
		public void startPaused() throws PogamutException {
			AgentModule.this.start(true);
		}
		
		@Override
		public void start() throws PogamutException {
			AgentModule.this.start(false);
		}
		
		@Override
		public void stop() throws PogamutException {
			AgentModule.this.stop();
		}
		
		@Override
		public void kill() {
			AgentModule.this.kill();
		}
		
		@Override
		public void reset() {
			AgentModule.this.reset();
		}		
		
		@Override
		public void pause() {
			AgentModule.this.pause();
		}
		
		@Override
		public void resume() {
			AgentModule.this.resume();
		}
		
	};
	
	/**
	 * Starts the agent module. (Called even if starting to paused state.)
	 */
	protected void start(boolean startToPaused) {
	}
	
	/**
	 * Stops the agent module.
	 * <p><p>
	 * Calls {@link AgentModule#cleanUp()}.
	 */
	protected void stop() {
		cleanUp();
	}
	
	/**
	 * Kills the agent module.
	 * <p><p>
	 * Calls {@link AgentModule#cleanUp()}.
	 */
	protected void kill() {
		cleanUp();
	}
	
	/**
	 * Pauses the agent module.
	 */
	protected void pause() {
	}

	/**
	 * Resumes the agent module.
	 */
	protected void resume() {
	}
	
	/**
	 * Resets the agent module so it may be reused.
	 * <p><p>
	 * Calls {@link AgentModule#cleanUp()}.
	 */
	protected void reset() {
		cleanUp();
	}
	
	/**
	 * Hook where to perform clean up of data structures of the module.
	 * <p><p>
	 * Called from {@link AgentModule#stop()}, {@link AgentModule#kill()}, {@link AgentModule#reset()}.
	 */
	protected void cleanUp() {		
		if (log != null && log.isLoggable(Level.INFO)) log.info("Cleaning up!");
	}

}
