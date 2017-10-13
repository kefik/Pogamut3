package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs;

import java.util.Collection;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.UT2004BatchAwareSharedWorldView;

@AgentTeamScoped

public class UT2004TestSharedWorldView extends UT2004BatchAwareSharedWorldView{

	public UT2004TestSharedWorldView(Logger logger) {
		super(logger);
	}
	
	@Override
	protected ISharedWorldObject createSharedObject(Class msgClass, WorldObjectId id, ITeamId teamId, TimeKey time)
	{
		Collection<ISharedProperty> c = getSharedProperties(id, teamId, time);
		//this creator implements testObjects + UTmessages
		return UT2004TestSharedObjectCreator.create(msgClass, id, c);
	}

}
