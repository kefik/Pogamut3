package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetSkin;

/**
 * Class providing Pogamut2 UT2004 configure commands. Changing of the bot
 * attributes like name, speed, etc. Changing the bot appearance.
 * 
 * @author Michal 'Knight' Bida
 */
public class ConfigureCommands extends BotCommands {

	/**
	 * Changes the configuration of the bot. How to use this. First you need to
	 * create your own Pogamut GB configuration command, you need to import
	 * cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.*; Then
	 * create your own Configuration command with new, set the fields you want
	 * to change and then use this method to send it to UT2004.
	 * 
	 * Example: Configuration conf = new Configuration();
	 * conf.setName("MyName"); configure(conf);
	 * 
	 * Note: The Id parameter cannot be changed, it is there for control server,
	 * so the server can configure the bots by the Id. Also some other
	 * configuration parameters may only be set when the game features
	 * particular rules (e.g. you cannot set invulnerability of the bot, when
	 * the cheating is off). See help in the documentation of Configuration
	 * command.
	 * 
	 * (issues GB CONF command)
	 * 
	 * @param config
	 *            Actual Pogamut GB Configuration command. Set all desired
	 *            fields you want to change.
	 * 
	 */
	public void configure(Configuration config) {
		agent.getAct().act(config);
	}

	/**
	 * Changes the bot appearance. Note that the bot will respawn after this
	 * command so the change of the skin can be visible.
	 * 
	 * Set the appearance through skin attribute (e.g. "HumanMaleA.MercMaleA").
	 * Find all packages and skins through unrealEd (Actor browser, search in
	 * UT2004/Animations folder) or look in GB user documentation on the web.
	 * Supported bot skins are Aliens (Aliens.), Bots (Bot.), human males
	 * (HumanMaleA.), human females (HumanFemaleA. ), juggernauts (Jugg.).
	 * Skaarj skins are not supported at the time being.
	 * 
	 * (issues GB SETSKIN command)
	 * 
	 * @param skin
	 *            New desired skin of the bot.
	 * 
	 */
	public void setBotAppearance(String skin) {
		agent.getAct().act(new SetSkin().setSkin(skin));
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public ConfigureCommands(UT2004Bot agent, Logger log) {
		super(agent, log);
	}

}