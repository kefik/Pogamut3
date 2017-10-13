package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

/**
 * Creates and wraps all available command modules. These command modules
 * contains documented methods that wraps Pogamut commands. For example simple
 * locomotion provides methods for basic bot movement in the environment.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
 * is called.
 * 
 * @author Knight
 */
public class CompleteBotCommandsWrapper {

	// Pointers to command modules, will be initialized in the constructor.

	Action action;

	AdvancedLocomotion locomotion;

	AdvancedShooting shooting;
	
	ImprovedShooting improvedShooting;

	Communication communication;

	ConfigureCommands configureCommands;

	SimpleRayCasting simpleRayCasting;

	Logger log;

	/**
	 * Returns {@link cz.cuni.amis.pogamut.ut2004.bot.commands.Action} command
	 * module.
	 * 
	 * @return action command module
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Returns
	 * {@link cz.cuni.amis.pogamut.ut2004.bot.commands.AdvancedLocomotion}
	 * command module.
	 * 
	 * @return advanced locomotion command module
	 */
	public AdvancedLocomotion getLocomotion() {
		return locomotion;
	}

	/**
	 * Returns {@link cz.cuni.amis.pogamut.ut2004.bot.commands.AdvancedShooting}
	 * command module.
	 * 
	 * @return advanced shooting command module
	 */
	public AdvancedShooting getShooting() {
		return shooting;
	}

	/**
	 * Returns {@link cz.cuni.amis.pogamut.ut2004.bot.commands.Communication}
	 * command module.
	 * 
	 * @return communication command module
	 */
	public Communication getCommunication() {
		return communication;
	}

	/**
	 * Returns
	 * {@link cz.cuni.amis.pogamut.ut2004.bot.commands.ConfigureCommands}
	 * command module.
	 * 
	 * @return configure commands command module
	 */
	public ConfigureCommands getConfigureCommands() {
		return configureCommands;
	}

	/**
	 * Returns {@link cz.cuni.amis.pogamut.ut2004.bot.commands.SimpleRayCasting}
	 * command module.
	 * 
	 * @return simple ray casting command module
	 */
	public SimpleRayCasting getSimpleRayCasting() {
		return simpleRayCasting;
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public CompleteBotCommandsWrapper(UT2004Bot agent, Weaponry weaponry, Logger log) {
		
		// initialize command modules
		this.log = log;
		if (this.log == null) {
			this.log = agent.getLogger().getCategory("Commands");
		}
		action = new Action(agent, log);
		locomotion = new AdvancedLocomotion(agent, log);
		shooting = new AdvancedShooting(agent, log);
		improvedShooting = new ImprovedShooting(weaponry, agent, log);
		communication = new Communication(agent, log);
		configureCommands = new ConfigureCommands(agent, log);
		simpleRayCasting = new SimpleRayCasting(agent, log);
	}

	public Logger getLog() {
		return log;
	}

	/**
	 * Returns {@link ImprovedShooting}.
	 * @return
	 */
	public ImprovedShooting getImprovedShooting() {
		return improvedShooting;
	}
}
