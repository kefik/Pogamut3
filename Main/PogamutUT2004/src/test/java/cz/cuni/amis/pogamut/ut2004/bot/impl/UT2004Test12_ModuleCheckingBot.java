package cz.cuni.amis.pogamut.ut2004.bot.impl;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.ModuleCheckingBot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.SimpleBotTest;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

public class UT2004Test12_ModuleCheckingBot extends UT2004Test {

    @Test
    public void test() {
    	UT2004BotFactory factory = new UT2004BotFactory(new UT2004BotModule(ModuleCheckingBot.class));
    	//BotTestContext testCtx = new BotTestContext(log, factory, ucc.getBotAddress());
    	BotTestContext testCtx = new BotTestContext(log, factory, new SocketConnectionAddress("localhost", 3000));
    	BotContext botCtx = testCtx.newBotContext();
    	new SimpleBotTest().run(botCtx);
    	
       	System.out.println("---/// TEST OK ///---");
    }
    
}
