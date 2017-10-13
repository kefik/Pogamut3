package cz.cuni.amis.pogamut.multi.communication.worldview.object;

import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;

/**
 * general interface for all sharedWorldObjects.
 * @author srlok
 *
 */
public interface ISharedWorldObject extends IWorldObject, Cloneable {

	/**
	 * Returns world time when the object was seen/updated for the last time.
	 * <p><p>
	 * The time suppose to be growing as the simulation carries on.
	 * <p>
	 * Always should be actual_timestamp >= any_prevously_recorded_timestamp,
	 * <p><p>
	 * object1.getLastSeenTime() > object2.getLastSeenTime() means that object1 last seen after object2
	 * 
	 * @return
	 */
	 public long getSimTime();
	
	 //Public ISharedWorldObjectUpdatedEvent createUpdateEvent( long time, ITeamId teamId);
	 
	public ISharedWorldObject clone();
	public ISharedProperty getProperty( PropertyId id);
	public Map<PropertyId, ISharedProperty> getProperties();
	public Class getCompositeClass();

}
