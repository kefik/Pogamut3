package cz.cuni.amis.pogamut.multi.worldview.stub;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenSharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;

public class EventDrivenSharedWorldViewStub extends EventDrivenSharedWorldView {

	public EventDrivenSharedWorldViewStub(Logger logger) {
		super(logger);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ISharedWorldObject createSharedObject(Class msgClass,
			WorldObjectId id, ITeamId teamId, TimeKey time) 
	{
		return SharedObjectCreatorStub.create( getSharedProperties(id, teamId, time) );
	}

}
