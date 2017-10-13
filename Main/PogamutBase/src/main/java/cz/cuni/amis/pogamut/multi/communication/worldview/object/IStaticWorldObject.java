package cz.cuni.amis.pogamut.multi.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;

/**
 * General interface for all staticWorldObjects.
 * @author srlok
 *
 */
public interface IStaticWorldObject extends IWorldObject {

	public Class getCompositeClass();
	
	/**
	 * This method is used for comparing if the static information has been changed during the simulation, 
	 * this method however takes into account the fact that some static information may be null for some time and then get filled later.
	 * Also this returns true if other is null.
	 * 
	 * WARNING use this method for anything else at your own risk!
	 * @param other
	 * @return
	 */
	public boolean isDifferentFrom(IStaticWorldObject other);

	//public IStaticWorldObjectUpdatedEvent createUpdateEvent( long time );

}
