package cz.cuni.amis.pogamut.multi.communication.translator.event;

import java.util.Collection;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;

/**
 * 
 * @author srlok
 *
 */
public interface ISharedWorldObjectUpdatedEvent extends IWorldChangeEvent {
	
	public WorldObjectId getId();
	
	/**
	 * This is required by SharedWorldView because objects might not have any sharedProperties but the SharedPart will still be required.
	 * @return
	 */
	public Class getCompositeObjectClass();
	
	public ITeamId getTeamId();
	
	public Collection<ISharedPropertyUpdatedEvent> getPropertyEvents();	
}
