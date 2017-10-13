package cz.cuni.amis.pogamut.base3d.worldview.object;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;

/**
 * General interface for objects whose visibility may change through time. 
 *
 * @author Juraj 'Loque' Simlovic
 * @author srlok
 */
public interface IViewable extends ICompositeWorldObject {
	/**
	 * Tells, whether the object is currently visible.
	 * 
	 * @return True if the object is visible; false otherwise.
	 */
	boolean isVisible();
	
}
