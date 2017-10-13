package cz.cuni.amis.pogamut.sposh.context;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004TCClient;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;
import cz.cuni.amis.pogamut.ut2004.vip.bot.VIPBotModule;
import cz.cuni.amis.pogamut.ut2004.vip.server.UT2004VIPServer;

/**
 * State context that contains extra addons:<br/>
 * -- UT2004 VIP, see {@link #vip} and {@link VIPBotModule}, {@link UT2004VIPServer}.
 * 
 * @author Jimmy
 */
public class UT2004AddonsContext<BOT extends UT2004Bot> extends UT2004Context<BOT> {

	protected VIPBotModule vip;
	
	public UT2004AddonsContext(String name, BOT bot) {
		super(name, bot);
	}

	protected void initializeModules(BOT bot) {
		super.initializeModules(bot);
		vip = new VIPBotModule(getBot(), getInfo(), getPlayers());
	}

	public VIPBotModule getVip() {
		return vip;
	}
	
}
