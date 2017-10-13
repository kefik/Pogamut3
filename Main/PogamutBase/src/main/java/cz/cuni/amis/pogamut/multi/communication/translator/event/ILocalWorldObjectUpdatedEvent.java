package cz.cuni.amis.pogamut.multi.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;

/**
 * 
 * @author srlok
 *
 */
public interface ILocalWorldObjectUpdatedEvent extends IWorldChangeEvent {

	public WorldObjectId getId();
	
	public IWorldObjectUpdateResult<ILocalWorldObject> update(ILocalWorldObject object);

}
