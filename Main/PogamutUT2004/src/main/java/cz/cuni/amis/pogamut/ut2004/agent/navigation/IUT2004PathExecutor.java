package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public interface IUT2004PathExecutor<PATH_ELEMENT extends ILocated> extends IUnrealPathExecutor<PATH_ELEMENT> {
	
	/**
	 * Merges current followed path with "morePath", i.e., it cuts off already passed elements and adds "morePath"
	 * to current path.
	 * 
	 * @param path
	 */
	public void extendPath(List<PATH_ELEMENT> morePath);
	
	/**
	 * Returns {@link NavPointNeighbourLink} the bot is currently running over.
	 * 
	 * Might be null if the link is unknown.
	 * 
	 * @return
	 */
	public NavPointNeighbourLink getCurrentLink();

	/**
	 * Returns how far is our target (path-distance).
	 * @return
	 */
	public double getRemainingDistance();	
}
