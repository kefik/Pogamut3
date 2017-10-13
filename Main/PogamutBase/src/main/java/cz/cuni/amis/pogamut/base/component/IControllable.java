package cz.cuni.amis.pogamut.base.component;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Interface providing a control methods for the implementors. Used almost everywhere 
 * in the Gavial libray.
 * 
 * @author ik
 */
@MXBean
@AgentScoped
public interface IControllable {
	
	/**
	 * Starts the object. If no exception is thrown we assume that the object has been correctly
	 * started and can be used.
	 * <p><p>
	 * May block.
	 * 
	 * @throws PogamutException
	 */
	public void start() throws PogamutException;
	
	/**
	 * Tries to stop the work of this object carefully.
	 * <p><p> 
	 * May block.
	 */
	public void stop();
	
	/**
	 * Kills the object - interrupt ruthlessly any work it might be doing.
	 * <p><p>
	 * This method should interrupt any threads the object may have.
	 * <p><p>  
	 * Must not block!
	 */
	public void kill();
	
}