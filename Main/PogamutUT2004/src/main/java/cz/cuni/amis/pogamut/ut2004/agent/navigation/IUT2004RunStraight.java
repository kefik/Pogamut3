package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;

/**
 * Interface for straight-runner that is combined with stuck-detectors.
 * 
 * @author Jimmy
 */
public interface IUT2004RunStraight {

	/**
	 * Whether the object is executing the running.
	 * @return
	 */
	public boolean isExecuting();
	
	/**
	 * Whether our run has succeeded (once we get to our target, this returns true).
	 * @return
	 */
	public boolean isSuccess();
	
	/**
	 * Whether our run has failed.
	 * @return
	 */
	public boolean isFailed();
	
	/**
	 * Get previous target of the straight-run.
	 * @return
	 */
	public ILocated getLastTarget();
	
	/**
	 * Get current target of the straight-run.
	 * @return
	 */
	public ILocated getCurrentTarget();
	
	/**
     * Sets focus of the bot when navigating (when using this object to run to some location target)!
     * To reset focus call this method with null parameter.
     * 
	 * @param focus
	 */
	public void setFocus(ILocated focus);
	
	/**
	 * Run along straight-line to some target.
	 * @param target
	 */
	public void runStraight(ILocated target);
	
	/**
	 * Stop the running.
	 */
	public void stop(boolean stopMovement);	
	
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
	 * Returns component's logger.
	 * @return
	 */
	public Logger getLog();
	
}
