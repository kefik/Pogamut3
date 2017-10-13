package cz.cuni.amis.pogamut.multi.worldview.stub;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareSharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchEndEventStub;


@AgentTeamScoped

public class BatchAwareSharedWVStub extends BatchAwareSharedWorldView{

	public BatchAwareSharedWVStub(Logger logger) {
		super(logger);
	}

	@Override
	protected boolean isBatchEndEvent(IWorldChangeEvent event) {
		return (event instanceof BatchEndEventStub);
	}
	
	
	@Override
	public void notify(IWorldChangeEvent event)
	{
		super.notify(event);
	}

	@Override
	protected ISharedWorldObject createSharedObject(Class msgClass,
			WorldObjectId id, ITeamId teamId, TimeKey time) {
		return SharedObjectCreatorStub.create( getSharedProperties(id, teamId, time));
	}

}
