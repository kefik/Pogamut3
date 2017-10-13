package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageTranslator;

/**
 * HideAndSeekMessagesTranslator
 * 
 * @author Jimmy
 */
public class HSMessagesTranslator extends ControlMessageTranslator {

	public HSMessagesTranslator(IWorldView worldView, boolean exceptionOnUnreadableMessage) {
		super(worldView, new HSMessages(), exceptionOnUnreadableMessage);
	}

}
