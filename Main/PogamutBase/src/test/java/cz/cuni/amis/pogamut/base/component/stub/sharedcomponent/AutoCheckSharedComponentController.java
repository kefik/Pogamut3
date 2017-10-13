package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentController;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.tests.ActivityLog;
import cz.cuni.amis.utils.exception.PogamutException;

public class AutoCheckSharedComponentController<COMPONENT extends ISharedComponent> extends SharedComponentController<COMPONENT> {

	private class EventListener implements IComponentEventListener<IComponentEvent> {

		private IAgentId agentId;

		public EventListener(IAgentId agentId) {
			this.agentId = agentId;
		}

		@Override
		public void notify(IComponentEvent event) {
			if (log.isLoggable(Level.INFO)) log.info("Got event: " + event);
			activity(EventToString.eventToString(agentId, event.getSource(), event.getClass()));
		}
		
	};
	
	private class CheckComponentControl extends SharedComponentControlHelper {
		
		@Override
		public void kill() {
			activity(MethodToString.kill(component));
		}
		
		@Override
		public void prePause() throws PogamutException {
			activity(MethodToString.prePause(component));
		}

		@Override
		public void preResume() throws PogamutException {
			activity(MethodToString.preResume(component));
		}

		@Override
		public void preStart() throws PogamutException {
			activity(MethodToString.preStart(component));
		}

		@Override
		public void preStartPaused() throws PogamutException {
			activity(MethodToString.preStartPaused(component));
		}

		@Override
		public void preStop() throws PogamutException {
			activity(MethodToString.preStop(component));
		}

		@Override
		public void pause() throws PogamutException {
			if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) > 0) {
				throw new RuntimeException("Can't pause(), there are components starting/resuming/running.");
			}
			activity(MethodToString.pause(component));
		}
		
		@Override
		public void reset() throws PogamutException {
			activity(MethodToString.reset(component));
		}

		@Override
		public void resume() throws PogamutException {
			if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == 0) {
				throw new RuntimeException("Can't resume(), there are NO components starting/resuming/running.");
			}
			activity(MethodToString.resume(component));
		}

		@Override
		public void start() throws PogamutException {
			if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == 0) {
				throw new RuntimeException("Can't start(), there are NO components starting/resuming/running.");
			}
			activity(MethodToString.start(component));
		}

		@Override
		public void startPaused() throws PogamutException {
			if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) == 0) {
				throw new RuntimeException("Can't startPause(), there are NO components starting-paused/pausing/paused.");
			}
			activity(MethodToString.startPaused(component));
		}

		@Override
		public void stop() throws PogamutException {
			if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED, ComponentState.STARTING_PAUSED) != 0) {
				throw new RuntimeException("Can't stop(), there are components starting/resuming/running/pausing/paused/starting-paused.");
			}
			activity(MethodToString.stop(component));
		}
		
		@Override
		public void localKill(IAgentId agentId) {
			activity(MethodToString.localKill(component, agentId));
		}

		@Override
		public void localPause(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPause(component, agentId));
		}

		@Override
		public void localPrePause(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPrePause(component, agentId));
		}

		@Override
		public void localPreResume(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPreResume(component, agentId));
		}

		@Override
		public void localPreStart(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPreStart(component, agentId));
		}

		@Override
		public void localPreStartPaused(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPreStartPaused(component, agentId));
		}

		@Override
		public void localPreStop(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localPreStop(component, agentId));
		}

		@Override
		public void localReset(IAgentId agentId) {
			activity(MethodToString.localReset(component, agentId));
		}

		@Override
		public void localResume(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localResume(component, agentId));
		}

		@Override
		public void localStart(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localStart(component, agentId));
		}

		@Override
		public void localStartPaused(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localStartPaused(component, agentId));
		}

		@Override
		public void localStop(IAgentId agentId) throws PogamutException {
			activity(MethodToString.localStop(component, agentId));
		}
		
	}
	
	private ActivityLog activityLog;

	private List<CheckSharedEvent> expect = new LinkedList<CheckSharedEvent>();

	private Map<IAgentId, EventListener> listeners = new HashMap<IAgentId, EventListener>();

	private boolean shouldBeChecking = true;
	
	public AutoCheckSharedComponentController(COMPONENT component, Logger log) {
		super(component, new SharedComponentControlHelper(), log);
		activityLog = new ActivityLog(log);
		this.control = new CheckComponentControl();
	}
	
	public boolean isShouldBeChecking() {
		return shouldBeChecking;
	}

	public void setShouldBeChecking(boolean shouldBeChecking) {
		this.shouldBeChecking = shouldBeChecking;
	}

		
	@Override
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus, ComponentDependencies dependencies) {
		EventListener listener = new EventListener(agentId);
		listeners.put(agentId, listener);
		bus.addEventListener(IComponentEvent.class, listener);
		super.addComponentBus(agentId, bus, dependencies);
	}
	
	@Override
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus) {
		super.removeComponentBus(agentId, bus);
		listeners.remove(agentId);
	}
	
	public void expectAnyOrder(String... activity) {
		activityLog.expectAnyOrder(activity);
	}
	
	public void expectExactOrder(String... activity) {
		activityLog.expectExactOrder(activity);
	}
	
	public void activity(String... activity) {
		if (shouldBeChecking) activityLog.activity(activity);
	}

	public void checkNoMoreActivityExpected() {
		if (shouldBeChecking) activityLog.checkNoMoreActivityExpected();
	}
	
}
