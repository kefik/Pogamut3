package cz.cuni.amis.pogamut.base.agent;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Interface providing a control methods for the implementors. Used almost everywhere 
 * in the Gavial libray.
 * <p><p>
 * Must remain compatible with {@link IStartable}. 
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
	 * May be blocking!
	 * 
	 * @throws PogamutException
	 */
	public void start() throws PogamutException;
	
	/**
	 * Tries to stop the work of this object carefully.
	 * <p><p> 
	 * May be blocking!
	 */
	public void stop();
	
	/**
	 * Kills the object - interrupt ruthlessly any work it might be doing.
	 * <p><p>
	 * This method should interrupt any threads the object may have.
	 * <p><p>  
	 * May be blocking!
	 */
	public void kill();

}