package cz.cuni.amis.pogamut.ut2004.bot.impl;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestLogic;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.SimpleBotTest;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

public class UT2004Test03_MindlessLogicBot extends UT2004Test {

    @Test
    public void test() {
    	UT2004BotFactory factory = new UT2004BotFactory(new UT2004BotModule(BotTestLogic.class));
    	BotTestContext testCtx = new BotTestContext(log, factory, ucc.getBotAddress());
    	BotContext botCtx = testCtx.newBotContext();
    	new SimpleBotTest().run(botCtx);
    	
       	System.out.println("---/// TEST OK ///---");
    }
    
}
