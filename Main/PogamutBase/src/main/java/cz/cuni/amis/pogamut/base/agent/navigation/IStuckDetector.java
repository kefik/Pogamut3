package cz.cuni.amis.pogamut.base.agent.navigation;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;

/**
 * Stuck detector provides a functionality to detect the situation in which the bot is unable
 * to reach its destination. Stuck detector does that (usually) based on some heuristics. 
 * 
 * @author Jimmy
 */
public interface IStuckDetector {

	/**
	 * Enable / Disable stuck detector. Default: FALSE (== disabled)
	 */
	public void setEnabled(boolean state);
	
	/**
	 * Tells the stuck detector, that the bot is waiting for something, thus the detector should not detect stuck!
	 * @param state
	 */
	public void setBotWaiting(boolean state);
	
	/**
	 * Where the bot is currently trying to get with DIRECT MOVEMENT (possibly with JUMPS).
	 * @param target
	 */
	public void setBotTarget(ILocated target);
	
	/**
	 * Tells whether the detector has detected a stuck.
	 * @return
	 */
	public boolean isStuck();
	
	/**
	 * Returns human-readable string describing why the bot has stuck.
	 * Must be non-null whenever {@link #isStuck()}, otherwise may contain null or arbitrary meaningless string.
	 * @return
	 */
	public String getStuckDetails();
	
	/**
	 * Restarts the detector - this method is called just before the executor
	 * starts to follow the path.
	 * <p><p>
	 * If {@link IStuckDetector#isStuck()} was reporting true, it should report 'false' after 
	 * the reset (until next stuck is detected).
	 */
	public void reset();
	
}
