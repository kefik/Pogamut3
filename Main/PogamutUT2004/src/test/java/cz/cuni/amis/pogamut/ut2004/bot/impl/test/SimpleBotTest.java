package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.communication.command.react.CommandReact;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.TurnTo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class SimpleBotTest extends AbstractBotTest {

	private long interleave;
	
	public SimpleBotTest() {
		interleave = 0;
	}

	public SimpleBotTest(long interleaveMillis) {
		this.interleave = interleaveMillis;
	}
	
	@Override
	protected void doTest(UT2004Bot bot, Logger log) {
		final CountDownLatch commandSentLatch = new CountDownLatch(1); 
		CommandReact<TurnTo> turnToReactionOnce = new CommandReact<TurnTo>(TurnTo.class, bot.getAct()) {
			@Override
			protected void react(TurnTo event) {
				commandSentLatch.countDown();
			}
		};
		
		WorldObjectFuture<Self> self = new WorldObjectFuture<Self>(bot.getWorldView(), Self.class);
		WorldObjectFuture<GameInfo> gameInfo = new WorldObjectFuture<GameInfo>(bot.getWorldView(), GameInfo.class);
		
		bot.start();
		
		if (bot.notInState(IAgentStateUp.class)) {
			bot.kill();
			throw new RuntimeException("Bot is not running!");
		}
        
        if (self.get(10000, TimeUnit.MILLISECONDS) == null) {
        	bot.kill();
            throw new RuntimeException("Self not present in the WorldView after 10 secs.");
        }
        
        if (gameInfo.get(10000, TimeUnit.MILLISECONDS) == null) {
        	bot.kill();
        	throw new RuntimeException("GameInfo not present in the WorldView after 10 secs.");
        }
        
        if (bot.getController() instanceof BotTestLogic) {
        	BotTestLogic botLogic = (BotTestLogic)bot.getController();
        	if (!botLogic.getLatch().await(10000, TimeUnit.MILLISECONDS)) {
        		bot.kill();
        		throw new RuntimeException("Self not received by LogicModule after 10 secs, even though Self received by the bot!");
        	}
        }
        if (bot.getController() instanceof BotModuleTestLogic) {
        	BotModuleTestLogic botLogic = (BotModuleTestLogic)bot.getController();
        	if (!botLogic.getLatch().await(10000, TimeUnit.MILLISECONDS)) {
        		bot.kill();
        		throw new RuntimeException("Self not received by LogicModule after 10 secs, even though Self received by the bot!");
        	}
        }
        
        bot.getAct().act(new TurnTo().setRotation(new Rotation(2000, 2000, 2000)));
        
       	try {
       		if (!commandSentLatch.await(1000, TimeUnit.MILLISECONDS)) {
       			bot.kill();
				throw new RuntimeException("Rotation command not sensed.");
			}
		} catch (InterruptedException e) {
			bot.kill();
			throw new PogamutInterruptedException(e, this);
		}
		
		if (bot.notInState(IAgentStateUp.class)) {
			bot.kill();
			throw new RuntimeException("Bot is not running and it still should!!!");
		}
		
		bot.stop();
		
		if (bot.getState().getFlag() instanceof IAgentStateFailed) {
			throw new RuntimeException("Bot failed due to previous exceptions.");
		}
		
		if (interleave > 0) {
			if (log.isLoggable(Level.WARNING)) log.warning("Going to sleep for "+ interleave + " ms...");
			try {
				Thread.sleep(interleave);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
		}
	}
	
}
