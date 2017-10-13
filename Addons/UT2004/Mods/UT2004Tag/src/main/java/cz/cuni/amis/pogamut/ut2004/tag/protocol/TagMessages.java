package cz.cuni.amis.pogamut.ut2004.tag.protocol;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessages;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ICustomControlMessage;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameEnd;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameRunning;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameStart;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPassed;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerImmunity;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerStatusChanged;

public class TagMessages extends ControlMessages {
	
	@SuppressWarnings("unchecked")
	public static final Class<? extends ICustomControlMessage>[] messages = 
		new Class[] {
			TagGameEnd.class,
			TagGameRunning.class,
			TagGameStart.class,
			TagPassed.class,
			TagPlayerImmunity.class,
			TagPlayerScoreChanged.class,
			TagPlayerStatusChanged.class,
		}
	;
	
	public TagMessages() {
		register(messages);
	}

}
