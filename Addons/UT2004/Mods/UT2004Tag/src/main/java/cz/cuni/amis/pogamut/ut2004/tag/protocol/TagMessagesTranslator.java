package cz.cuni.amis.pogamut.ut2004.tag.protocol;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageTranslator;

public class TagMessagesTranslator extends ControlMessageTranslator {

	public TagMessagesTranslator(IWorldView worldView, boolean exceptionOnUnreadableMessage) {
		super(worldView, new TagMessages(), exceptionOnUnreadableMessage);
	}

}
