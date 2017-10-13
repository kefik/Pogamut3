package cz.cuni.amis.pogamut.ut2004.vip.protocol;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageTranslator;

/**
 * CSMessagesTranslator
 * 
 * @author Jimmy
 */
public class CSMessagesTranslator extends ControlMessageTranslator {

	public CSMessagesTranslator(IWorldView worldView, boolean exceptionOnUnreadableMessage) {
		super(worldView, new CSMessages(), exceptionOnUnreadableMessage);
	}

}
