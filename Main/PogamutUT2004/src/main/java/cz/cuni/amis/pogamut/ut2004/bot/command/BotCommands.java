package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

/**
 * Basic abstract class that is the ancestor of classes that provide wrapped UT
 * bot commands.
 * 
 * @author Michal 'Knight' Bida
 */
public abstract class BotCommands extends SensomotoricModule<UT2004Bot> {

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	protected BotCommands(UT2004Bot agent, Logger log) {
		super(agent, log);
	}

	protected BotCommands(UT2004Bot agent) {
		super(agent);
	}
}
