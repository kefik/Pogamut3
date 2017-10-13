package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.mediator.impl.Mediator;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.UT2004LockableLocalWorldView;

public class UT2004TestLocalWorldView extends UT2004LockableLocalWorldView
{

	public UT2004TestLocalWorldView(ComponentDependencies dependencies, IMediator mediator,
			ILifecycleBus bus, IAgentLogger logger,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId)
	{
	
		super(dependencies, bus, logger, mediator, parentWorldView, agentId);
	}
	
	@Override
	protected ICompositeWorldObject createCompositeObject(ILocalWorldObject localObject,ISharedWorldObject sharedObject,IStaticWorldObject staticObject)
	{
		return UT2004TestCompositeObjectCreator.createObject(localObject, sharedObject, staticObject);
	}
	

}
