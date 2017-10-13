package cz.cuni.amis.pogamut.multi.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;

/**
 * Interface for all shared properties in the world. <br>
 * <p>
 * A shared property is subjective to a single team of game bots but it is objective to all bots in the same team. Generally, the shared properties
 * will contain information, that is useful to the entire team and can be derived from single-bot observation. ( visible => spawned ; mover positions etc...).
 * These inferred values can get dirty if the direct observation of the object is older than the origin of the property.</p>
 * <p>Also, sharedProperties can be used for team-coordination information, orders and such. In this case, the properties generally won't get dirty.</p>
 * @author srlok
 *
 */
public interface ISharedProperty<TYPE> extends Cloneable {

	//TODO simTime for properties
	
	/**
	 * Returns value of the property.
	 * @return
	 */
	public TYPE getValue();
	
	/**
	 * Sets the value of the property - should be used by Pogamut Library developers only!
	 * @param value
	 */
	public void setValue(TYPE value);
	
	/**
	 * Must return an exact duplicate of this ISharedProperty, this will be used
	 * to create old versions of SharedProperties in the worldView.
	 * @return
	 */
	public ISharedProperty clone();
	
	/**
	 * ObjectId of the object that this property is part of.
	 * @return
	 */
	public WorldObjectId getObjectId();
	
	/**
	 * Unique Id of this property.
	 * @return
	 */
	public PropertyId getPropertyId();
	
	/**
	 * SharedProperties are generally properties derived from bot's observation of the world. (like visible => spawned ). This may become a problem when the inferred
	 * information is outdated (ie. a bot can no longer see the spawned item, but the last time anybody from the team saw it, it was spawned). In this case, we consider
	 * the information "dirty", because the team only assumes the value. It can be wrong. (the game will always know the correct value of course, only our AI won't.
	 * @return
	 */
	public boolean isDirty();
	
	/**
	 * Class of the compositeObject this property belongs to.
	 * @return
	 */
	public Class<?> getCompositeClass();
	
	/**
	 * Returns class of the property value. Used to enforce value-class checks.
	 * @return
	 */
	public Class<?> getPropertyValueClass();

	/**
	 * Creates an event updating a sharedProperty of the same id and the specified team to the same value as the parent property.
	 * @param time
	 * @param teamId
	 * @return
	 */
	public ISharedPropertyUpdatedEvent createUpdateEvent(long time, ITeamId teamId);
	
	public boolean nullOverrides();
}
