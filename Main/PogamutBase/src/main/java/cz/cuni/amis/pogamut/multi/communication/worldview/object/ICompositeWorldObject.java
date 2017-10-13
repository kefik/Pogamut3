package cz.cuni.amis.pogamut.multi.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;

/**
 * General interface for all compositeWorldObjects
 * Composite world objects are the equivalent of old WorldObjects.
 * @author srlok
 *
 */
public interface ICompositeWorldObject extends IWorldObject {
	
	
	
	 /* Creates a update event, that will update a CompositeObject of the same type and id to the object on which the method
	 * was called.
	 * @param time event time
	 * @param teamId teamId of the team this object belongs to
	 * @return updateEvent
	 */
	//public ICompositeWorldObjectUpdatedEvent createUpdateEvent( long time, ITeamId teamId );
	
	public ILocalWorldObject  getLocal();
	public ISharedWorldObject getShared();
	public IStaticWorldObject getStatic();
}
