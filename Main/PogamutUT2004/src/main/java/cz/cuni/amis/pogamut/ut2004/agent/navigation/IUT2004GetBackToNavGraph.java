package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * This class is meant to provide easy "get-back-to-navigation-graph-in-order-I-can-safely-navigate-through-map"
 * implementation. 
 * 
 * @author Jimmy
 */
public interface IUT2004GetBackToNavGraph {	
		
	/**
	 * Returns nearest {@link NavPoint} to current bot's location.
	 * 
	 * @return
	 */
	public NavPoint getNearestNavPoint();
	
	/**
	 * Whether the bot is currently on some {@link NavPoint}.
	 */
	public boolean isOnNavGraph();
	
	/**
	 * Whether the class is executing (== working == trying to get the bot back on NavGraph == to make {@link UT2004GetBackToNavGraph#isOnNavGraph()} true).
	 * @return
	 */
	public boolean isExecuting();
	
	/**
     * Sets focus of the bot when navigating (when using this object to run to some location target)!
     * To reset focus call this method with null parameter.
     * 
     * @param located
     */
    public void setFocus(ILocated located);
    
	/**
	 * Start the class, let it take the bot back to navigation graph.
	 */
	public void backToNavGraph();
	/**
	 * Stop the class.
	 */
	public void stop();
		
	/**
	 * Adds another stuck detector to be used for stuck detection :)
	 * @param stuckDetector
	 */
	public void addStuckDetector(IStuckDetector stuckDetector);
	
	/**
	 * Removes stuck detector.
	 * @param stuckDetector
	 */
	public void removeStuckDetector(IStuckDetector stuckDetector);
	
	/**
	 * Removes ALL stuck detectors.
	 */
	public void clearStuckDetectors();

	/**
	 * Returns component logger. 
	 * @return
	 */
	public LogCategory getLog();
	
}
