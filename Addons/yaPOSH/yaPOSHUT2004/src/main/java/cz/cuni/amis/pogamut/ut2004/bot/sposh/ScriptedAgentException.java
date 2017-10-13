package cz.cuni.amis.pogamut.ut2004.bot.sposh;

import cz.cuni.amis.utils.exception.PogamutException;



/**
 * This exception is raised by ScriptedAgent and it's descendants.
 * It usualy means that your scripts don't have required methods
 * implemented or have syntax / logic errors.
 *
 * @author Jimmy
 */
@Deprecated
public class ScriptedAgentException extends PogamutException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message
	 */
	public ScriptedAgentException(String message){
		super(message, null);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * @param message
	 * @param cause
	 */
	public ScriptedAgentException(String message, Throwable cause) {
		super(message, cause, null);
	}

}

