package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.logging.Logger;

import cz.cuni.amis.utils.exception.PogamutException;

public class UT2004AStarPathTimeoutException extends PogamutException {

	public UT2004AStarPathTimeoutException(String message, Logger log, Object origin) {
		super(message, log, origin);
	}

}
