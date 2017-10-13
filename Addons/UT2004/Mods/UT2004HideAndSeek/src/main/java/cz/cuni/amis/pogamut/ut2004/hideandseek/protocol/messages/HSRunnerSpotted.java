package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.UT2004HSServer;

/**
 * Seeker has spotted {@link HSRunnerSpotted#getBotId()} and may capture him by running into the safe area before it.
 * <p><p>
 * NOTE THAT THE SEEKER CANNOT CAPTURE THE RUNNER BEFORE THIS MESSAGE IS BROADCAST!
 * {@link UT2004HSServer} implements delay between "runner is visible" and "seeker is spotted" to allow "peeking-around-the-corner" behavior for
 * the seeker.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="HS_RUNNER_SPOTTED")
public class HSRunnerSpotted extends HSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSRunnerSpotted() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSRunnerSpotted[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}


}
