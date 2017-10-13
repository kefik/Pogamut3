package cz.cuni.amis.pogamut.ut2004.bot.impl;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Issued by {@link UT2004Bot} itself right at the beginning of {@link UT2004Bot#initCommandRequested()} that allows to finish initialization
 * of modules that depends on navigation graphs and other stuff.
 *  
 * @author Jimmy
 */
public class BotAboutToBeSpawnedForTheFirstTime implements IWorldChangeEvent, IWorldEvent {

	@Override
	public long getSimTime() {
		return 0;
	}

}
