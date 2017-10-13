package cz.cuni.amis.pogamut.base.communication.translator;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.exception.TranslatorException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

/**
 * This is handler for messages that should produce IWorldChangeEvent(s) out of
 * messages we receive from the world.
 * @author Jimmy
 */
public interface IWorldMessageTranslator {

	/**
	 * The handler must process the message and return 0,1 or more world events.
	 * @param message
	 * @return
	 */
	public IWorldChangeEvent[] processMessage(InfoMessage message) throws TranslatorException, ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Reinitialize the translator.
	 */
	public void reset();
	
}
