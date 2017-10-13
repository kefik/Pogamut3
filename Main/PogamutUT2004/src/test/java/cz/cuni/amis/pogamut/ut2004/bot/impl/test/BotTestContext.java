package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.test.TestContext;

public class BotTestContext extends TestContext {
	
	private static int BOT_NUM = 0;
	
	private static final Object MUTEX = new Object();	

	private IAgentFactory factory;
	private SocketConnectionAddress address;

	public BotTestContext(Logger log, IAgentFactory factory, SocketConnectionAddress address) {
		super(log);
		this.factory = factory;
		NullCheck.check(this.factory, "factory");
		this.address = address;
		NullCheck.check(this.address, "address");
	}
	
	public BotContext newBotContext() {
		int num;
		UT2004Bot bot;
		synchronized(MUTEX) {
			num = ++BOT_NUM;
			bot = (UT2004Bot) factory.newAgent(new UT2004BotParameters().setAgentId(new AgentId("TestBot" + num)).setWorldAddress(address));
		}		
		bot.getLogger().addDefaultConsoleHandler();
		bot.getLogger().setLevel(Level.FINE);
		return new BotContext(bot);
	}
	
}
