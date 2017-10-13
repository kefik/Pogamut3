package cz.cuni.amis.pogamut.base3d.worldview.object;

/**
 * General interface for objects whose reachability may change through time. 
 *
 * @author Juraj 'Loque' Simlovic
 */
public interface IReachable
{
	/**
	 * Tells, whether the object is currently reachable.
	 * 
	 * @return True if the object is reachable; false otherwise.
	 */
	boolean isReachable();
	
}
