package cz.cuni.amis.pogamut.ut2004.analyzer;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;

/**
 * Analyzer's observer is an agent that observes some bot inside the environment collecting
 * interesting information about him.
 * <p><p>
 * It runs totally independently of the actual {@link UT2004Bot} instance and may even be started
 * in different JVM (as we're interacting with it only via GameBots2004 protocol).
 */
public interface IUT2004AnalyzerObserver extends IUT2004Observer {

	/**
	 * Returns id of the bot that the observer is sniffing info from.
	 * 
	 * @return bot's id
	 */
	public UnrealId getObservedBotId();
	
}
