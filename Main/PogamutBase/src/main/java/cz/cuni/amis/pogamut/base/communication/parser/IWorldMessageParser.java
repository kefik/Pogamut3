package cz.cuni.amis.pogamut.base.communication.parser;

import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

/**
 * Message parser has a method for reading and parsing messages from the world.
 * <p><p>
 * It should be initialized with IWorldReaderProvider and implement serialization of the
 * message to string.
 * 
 * @author Jimmy
 */
public interface IWorldMessageParser extends IComponent {
	
	/**
	 * May block.
	 * <BR><BR>
	 * Should get and parse next message from the reader.
	 * 
	 * @return parsed message
	 */
	public InfoMessage parse() throws CommunicationException, ComponentNotRunningException, ComponentPausedException;
	
}