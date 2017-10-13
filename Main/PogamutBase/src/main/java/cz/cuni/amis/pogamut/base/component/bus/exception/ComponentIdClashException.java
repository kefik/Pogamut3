package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.utils.token.IToken;

public class ComponentIdClashException extends ComponentBusException {
	
	public ComponentIdClashException(IToken tokenClash, Logger log, IComponentBus origin) {
		super("Two components tried to register under one id: " + tokenClash.getToken(), log, origin);
	}

}
