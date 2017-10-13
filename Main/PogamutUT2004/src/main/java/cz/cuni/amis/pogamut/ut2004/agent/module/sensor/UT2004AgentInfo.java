package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;

public class UT2004AgentInfo extends AgentInfo {

	public ItemType getCurrentWeaponType() {
		if (getSelf() == null)
			return null;
		return UT2004ItemType.getItemType(getCurrentWeaponName());
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * 
	 * @param game
	 *            game info module
	 */
	public UT2004AgentInfo(UT2004Bot bot) {
		this(bot, new Game(bot), null);
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * 
	 * @param bot
	 *            owner of the module
	 * @param game
	 *            game info module
	 */
	public UT2004AgentInfo(UT2004Bot bot, Game game) {
		this(bot, game, null);
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * 
	 * @param bot
	 *            owner of the module
	 * @param game
	 *            game info module
	 * @param log
	 *            Logger to be used for logging runtime/debug info. Note: If
	 *            <i>null</i> is provided, this memory module creates it's own
	 *            logger.
	 */
	public UT2004AgentInfo(UT2004Bot bot, Game game, Logger log)

	{
		super(bot, game, log);
	}
}
