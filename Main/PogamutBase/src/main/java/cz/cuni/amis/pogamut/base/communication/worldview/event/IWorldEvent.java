/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.communication.worldview.event;

import cz.cuni.amis.utils.listener.Event;

/**
 * General interface for events occurring in the world.
 * <p><p> 
 * Source of these events is {@link IWorldView}.
 * 
 * @author ik
 * @author srlok
 */
public interface IWorldEvent extends Event {

	/**
	 * Returns the simulation time when the event has occurred.
	 * @return
	 */
	public long getSimTime();
	
}
