package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalViewable;

/**
 * Interface for all UT2004 local viewable objects. Adds capability to create disappearEvents.
 * @author srlok
 *
 */
public interface ILocalGBViewable extends ILocalViewable{
	
	/**
	 * Creates an update event that has to update visibility to false.
	 * @return
	 */
	public ILocalWorldObjectUpdatedEvent createDisappearEvent();
}
