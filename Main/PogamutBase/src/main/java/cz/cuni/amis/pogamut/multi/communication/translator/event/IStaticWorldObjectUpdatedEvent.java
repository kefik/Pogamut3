package cz.cuni.amis.pogamut.multi.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;

public interface IStaticWorldObjectUpdatedEvent extends IWorldChangeEvent {

	/**
	 * Only possible results are CREATED, DESTROYED or SAME
	 * static objects remain the same so they cannot be updated.
	 * @return
	 */
	public IWorldObjectUpdateResult<IStaticWorldObject> update(IStaticWorldObject object);

	public WorldObjectId getId();
}