package cz.cuni.amis.pogamut.multi.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

/**
 * Interface for all compositeObject updates in the world.
 * Composite updates return no result by themselves, only the partial updates do.
 * @author srlok
 *
 */
public interface ICompositeWorldObjectUpdatedEvent extends IWorldChangeEvent {
	
	/**
	 * Returns WorldObjectId of the updatedObject.
	 * @return
	 */
	public WorldObjectId getId();

	/**
	 * Returns the event updating the local part of the object.
	 * @return
	 */
	public ILocalWorldObjectUpdatedEvent getLocalEvent();
	
	/**
	 * Returns the event updating the shared part of the object.
	 * @return
	 */
	public ISharedWorldObjectUpdatedEvent getSharedEvent();
	
	/**
	 * Returns the update related to the static part of the object.
	 * Static updates are only possible if they create or destroy the object. (Updating the static part doesnt even make sense,
	 * the static part stays the same from definition.) 
	 * If the event does not create/destroy the static object part, this should return null.
	 * @return
	 */
	public IStaticWorldObjectUpdatedEvent getStaticEvent(); //we need this for CREATED and DESTROYED events

	
}
