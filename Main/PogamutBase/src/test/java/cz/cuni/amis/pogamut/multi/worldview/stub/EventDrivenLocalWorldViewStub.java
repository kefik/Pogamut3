package cz.cuni.amis.pogamut.multi.worldview.stub;

import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;

public class EventDrivenLocalWorldViewStub extends EventDrivenLocalWorldView {

	public EventDrivenLocalWorldViewStub(ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger, ISharedWorldView sharedWV,
			ITeamedAgentId agentId) {
		super(dependencies, bus, logger, sharedWV, agentId);
	}

	@Override
	protected ICompositeWorldObject createCompositeObject(
			ILocalWorldObject localObject, ISharedWorldObject sharedObject,
			IStaticWorldObject staticObject) 
	{
		return CompositeObjectCreatorStub.create(localObject, sharedObject, staticObject);
	}

}
