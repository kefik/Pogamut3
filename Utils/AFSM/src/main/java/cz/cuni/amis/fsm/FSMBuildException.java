package cz.cuni.amis.fsm;

import java.util.logging.Logger;

/**
 * Exception that is thrown whenever an error occurs during FSM instantiation. Note that this
 * is RuntimeException. We assume that you will tune your FSM and don't what to bother later
 * with catching those exceptions (as they can't be produced...).
 * 
 * @author Jimmy
 */
public class FSMBuildException extends RuntimeException {
	
	/**
	 * @param msg
	 * @param log may be null
	 */
	public FSMBuildException(String msg, Logger log) {
		super(msg);
		if (log != null) log.severe("Exception: " + msg);
	}

}
