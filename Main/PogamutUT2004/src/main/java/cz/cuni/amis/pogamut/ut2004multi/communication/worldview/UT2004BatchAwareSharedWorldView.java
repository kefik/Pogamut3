package cz.cuni.amis.pogamut.ut2004multi.communication.worldview;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareSharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004SharedObjectCreator;

@AgentTeamScoped

/**
 * Implements the capability to recognize BeginMessages, EndMessages and create the UT2004 shared objects from sharedProperties.
 */
public class UT2004BatchAwareSharedWorldView extends BatchAwareSharedWorldView{

	@Inject
	public UT2004BatchAwareSharedWorldView(Logger logger) {
		super(logger);
	}
	
	@Override
	protected boolean isBatchEndEvent(IWorldChangeEvent event) {
		return event instanceof EndMessage;
	}

	@Override
	protected ISharedWorldObject createSharedObject(Class msgClass,
			WorldObjectId id, ITeamId teamId, TimeKey time) 
	{
		
		Collection<ISharedProperty> properties = this.getSharedProperties(id, teamId, time);
		return UT2004SharedObjectCreator.create( msgClass , id , properties);
	}

	
}
