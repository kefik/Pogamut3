package cz.cuni.amis.pogamut.multi.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;

/**
 * 
 * @author srlok
 *
 */
public interface ISharedPropertyUpdatedEvent extends IWorldChangeEvent {
	
	public WorldObjectId getObjectId();
	public PropertyId getPropertyId();
	public ITeamId getTeamId();
	
	public ISharedPropertyUpdateResult update( ISharedProperty property );

}
