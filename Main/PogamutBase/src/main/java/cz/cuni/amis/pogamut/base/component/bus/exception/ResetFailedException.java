package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;

public class ResetFailedException extends ComponentBusException {
	
	public ResetFailedException(Throwable cause, Logger log, IComponentBus origin) {
		super("Reset failed: " + cause.getMessage(), cause, log, origin);
	}

}
