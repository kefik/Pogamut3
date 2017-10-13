package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.TCMinaClient;

public enum TCRecipient {
	
	GLOBAL,
	TEAM,
	CHANNEL,
	PRIVATE,
	
	/**
	 * Special type of the recipient - sent by {@link TCMinaServer} and consumed by {@link TCMinaClient}.
	 * These messages are usually consumed by {@link TCMinaClient} but some of them are even propagated to the {@link UT2004Bot}.
	 */
	TC_INFO,
	
	/**
	 * Special type of the recipient - sent by {@link TCMinaClient} and consumed by {@link TCMinaServer}.
	 * These messages are never received by {@link TCMinaClient} and thus they are NEVER further propagated to the {@link UT2004Bot}.
	 */
	TC_REQUEST
}
