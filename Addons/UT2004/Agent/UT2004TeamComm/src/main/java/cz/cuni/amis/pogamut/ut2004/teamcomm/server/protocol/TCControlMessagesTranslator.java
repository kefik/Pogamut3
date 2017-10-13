package cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageTranslator;

public class TCControlMessagesTranslator extends ControlMessageTranslator {

	public TCControlMessagesTranslator(IWorldView worldView, boolean exceptionOnUnreadableMessage) {
		super(worldView, new TCControlMessages(), exceptionOnUnreadableMessage);
	}

}
