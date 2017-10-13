package cz.cuni.amis.pogamut.ut2004.teamcomm.bot;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;

public class UT2004BotTCController<BOT extends UT2004Bot> extends UT2004BotModuleController<BOT> {

	protected UT2004TCClient tcClient;
		
	protected void initializeModules(BOT bot) {
		super.initializeModules(bot);
		tcClient = new UT2004TCClient(bot, bot.getWorldView());		
	}
	
	/**
	 * Team-Communication client, auto-connects to {@link UT2004TCServer} once the server advertised itself via {@link TCControlServerAlive} message.
	 * @return
	 */
	public UT2004TCClient getTCClient() {
		return tcClient;
	}
	
}
