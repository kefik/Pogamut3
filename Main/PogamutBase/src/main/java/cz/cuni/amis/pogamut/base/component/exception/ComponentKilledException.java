package cz.cuni.amis.pogamut.base.component.exception;

import cz.cuni.amis.pogamut.base.component.IComponent;

public class ComponentKilledException extends ComponentException {
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public ComponentKilledException(IComponent component, Object origin) {
		super("Component " + component.getComponentId().getToken() + " has been killed.", origin);
	}

}
