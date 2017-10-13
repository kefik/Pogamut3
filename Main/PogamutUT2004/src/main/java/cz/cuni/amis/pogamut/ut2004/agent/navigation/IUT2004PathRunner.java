package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/**
 * Runner is actually responsible for running directly between two locations.
 * 
 * 
 * @author Jimmy
 *
 */
public interface IUT2004PathRunner {

	/**
	 * Resets the runner state.
	 * <p><p>
	 * Called before the navigator turns its attention to another location.
	 */
	public void reset();
	
	/**
	 * Called iteratively to reach the 'firstLocation'. This method should get the bot to the 'firstLocation'.
	 * <p><p>
	 * The 'secondLocation' is the location that will most likely be pursued next.
	 * 
	 * @param runningFrom location we're running from (i.e., where the bot may safely return), may be null
	 * @param firstLocation where the bot should run to
	 * @param secondLocation where the bot will the most likely continue its run
	 * @param focus where the bot should be looking while running
     * @param navPointsLink if we are traveling between two NavPoints connected by a link, we will receive the link with movement information here
     * @param reachable NOT USED ANYMORE
     * @param forceNoJump runner MUST NOT jump
	 * @return
	 */
	public boolean runToLocation(Location runningFrom, Location firstLocation, Location secondLocation, ILocated focus, NavPointNeighbourLink navPointsLink, boolean reachable, boolean forceNoJump);
	
}
