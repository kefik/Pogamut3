package cz.cuni.amis.pogamut.multi.worldview.stub;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchBeginEventStub;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchEndEventStub;

public class BatchAwareLocalWVStub extends BatchAwareLocalWorldView{

	public BatchAwareLocalWVStub(ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId) {
		super(dependencies, bus, logger, parentWorldView, agentId);		
	}

	@Override
	public boolean isRunning() {
		return super.isRunning();
	}
	
	@Override
	protected boolean isBatchBeginEvent(IWorldChangeEvent event) {
		return (event instanceof BatchBeginEventStub);
	}

	@Override
	protected boolean isBatchEndEvent(IWorldChangeEvent event) {
		return (event instanceof BatchEndEventStub);
	}

	@Override
	protected ICompositeWorldObject createCompositeObject(ILocalWorldObject localObject,
			ISharedWorldObject sharedObject, IStaticWorldObject staticObject) 
	{
		return CompositeObjectCreatorStub.create(localObject, sharedObject, staticObject);
	}

	@Override
	protected void disappearObject(WorldObjectId id, long time) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
