package cz.cuni.amis.pogamut.multi.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;

/**
 * General interface for all localWorldObjects
 * @author srlok
 *
 */
public interface ILocalWorldObject extends IWorldObject, Cloneable {
	
	public ILocalWorldObject clone();
	
	//public ILocalWorldObjectUpdatedEvent createUpdateEvent( long time);
	
	public Class getCompositeClass();
	
	
}