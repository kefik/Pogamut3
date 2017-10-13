package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;

/**
 * Marks that fatal error has happened that prevents the component from running.
 * <p><p>
 * It is strongly advised to override toString() as in ${link {@link FatalErrorEvent}. 
 */
public interface IFatalErrorEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {
	
	/**
	 * Returns description of what went wrong.
	 * @return
	 */
	public String getMessage();
	
	/**
	 * Exception associated with the error, may be null. 
	 * @return
	 */
	public Throwable getCause();
		
	/**
	 * Stack trace of the error - first element of the stacktrace should be the place where
	 * the fatal error event has been created.
	 * @return
	 */
	public StackTraceElement[] getStackTrace();
	
	/**
	 * Called to get a long human-readable description of the fatal error.
	 */
	public String getSummary();
	
}
