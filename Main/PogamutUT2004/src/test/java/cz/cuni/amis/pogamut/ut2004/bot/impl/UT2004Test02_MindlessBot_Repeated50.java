package cz.cuni.amis.pogamut.ut2004.bot.impl;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.SimpleBotTest;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.utils.test.Repeater;

public class UT2004Test02_MindlessBot_Repeated50 extends UT2004Test {

    @Test
    public void test() {
    	UT2004BotFactory factory = new UT2004BotFactory(new UT2004BotModule(UT2004BotController.class));
    	BotTestContext testCtx = new BotTestContext(log, factory, ucc.getBotAddress());
    	BotContext botCtx = testCtx.newBotContext();
    	new Repeater<BotContext>(50, new SimpleBotTest(2000)).run(botCtx);
    	
       	System.out.println("---/// TEST OK ///---");
    }
    
}
