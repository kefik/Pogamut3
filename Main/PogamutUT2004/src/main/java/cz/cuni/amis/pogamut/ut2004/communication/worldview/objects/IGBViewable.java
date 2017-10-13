package cz.cuni.amis.pogamut.ut2004.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;

public interface IGBViewable extends IViewable {
	
	/**
	 * Creates an update event that has to update visibility to false.
	 * @return
	 */
	public IWorldObjectUpdatedEvent createDisappearEvent();

}
