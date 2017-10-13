package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessages;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ICustomControlMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSAssignSeeker;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerCaptured;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerFouled;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSafe;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSpotted;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSurvived;

/**
 * HideAndSeekMessages
 * 
 * @author Jimmy
 */
public class HSMessages extends ControlMessages {
	
	@SuppressWarnings("unchecked")
	public static final Class<? extends ICustomControlMessage>[] messages = 
		new Class[] {
			HSAssignSeeker.class,
			HSBotStateChanged.class,
			HSGameEnd.class,
			HSGameStart.class,
			HSPlayerScoreChanged.class,
			HSRoundEnd.class,
			HSRoundState.class,
			HSRoundStart.class,
			HSRunnerCaptured.class,
			HSRunnerFouled.class,
			HSRunnerSafe.class,
			HSRunnerSpotted.class,
			HSRunnerSurvived.class,
		}
	;
	
	public HSMessages() {
		register(messages);
	}

}
