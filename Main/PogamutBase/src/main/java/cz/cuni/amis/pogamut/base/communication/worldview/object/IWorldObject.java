package cz.cuni.amis.pogamut.base.communication.worldview.object;

/**
 * Interface for all objects that can be found in the world.
 *  
 * @author Jimmy
 */
public interface IWorldObject {
	
	/**
	 * Returns an id of the object that is unique among all world objects.
	 * @return
	 */
	public WorldObjectId getId();
	
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
	
}
