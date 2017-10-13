package cz.cuni.amis.pogamut.ut2004multi.communication.worldview;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfLocal;

/**
 * Implements the capability to recognize begin and end events and thus brings the capability to lock/unlock worldView. 
 * @author srlok
 *
 */
@AgentScoped
public class UT2004LockableLocalWorldView extends UT2004VisionLocalWorldView {

	@Inject
	public UT2004LockableLocalWorldView(
			@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger, IMediator mediator,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId) {
		super(dependencies, bus, logger, parentWorldView, agentId);
		mediator.setConsumer(this);
	}

	// because of SELF we have to override the objectUpdatedBehavior
	/*
	 * @Override protected void objectCreated(ILocalWorldObject obj, long
	 * simTime) { super.objectCreated(obj, simTime); }
	 * 
	 * @Override protected void objectUpdated(ILocalWorldObject obj, long
	 * simTime) { if (obj instanceof SelfLocal) { raiseEvent( new
	 * WorldObjectUpdatedEvent<Self>(this.getSingle(Self.class),
	 * getCurrentTimeKey().getTime()) ); } super.objectUpdated(obj, simTime); }
	 */

	@Override
	protected boolean isBatchBeginEvent(IWorldChangeEvent event) {
		return event instanceof BeginMessage;
	}

	@Override
	protected boolean isBatchEndEvent(IWorldChangeEvent event) {
		return event instanceof EndMessage;
	}

}
