package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateResumed;
import cz.cuni.amis.pogamut.base.communication.command.react.CommandReact;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DisconnectBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.TurnTo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class PauseResumeBotTest extends AbstractBotTest {

	private long interleave;

	/**
	 * 0 == bot.stop()
	 * 1 == bot.kill()
	 * 2 == bot.getAct().act(new DisconnectBot())
	 */
	private int killMethod;
	
	public PauseResumeBotTest(int killMethod) {
		interleave = 0;
		this.killMethod = killMethod;
		if (this.killMethod < 0 || this.killMethod > 2) throw new RuntimeException("killMethod can be 0 ... bot.stop(), 1 ... bot.kill() or 2 ... bot.getAct().act(new DisconnectBot()) NOT: " + this.killMethod);
	}

	public PauseResumeBotTest(long interleaveMillis) {
		this.interleave = interleaveMillis;
	}
	
	@Override
	public void doTest(UT2004Bot bot, Logger log) {
		bot.start();

        WorldObjectFuture<Self> self = new WorldObjectFuture<Self>(bot.getWorldView(), Self.class);
        
        if (self.get(10000, TimeUnit.MILLISECONDS) == null) {
        	bot.kill();
            throw new RuntimeException("Self not present in the WorldView after 10 secs.");
        }
		
        if (bot.getController() instanceof BotTestLogic) {
        	BotTestLogic botLogic = (BotTestLogic)bot.getController();
        	if (!botLogic.getLatch().await(10000, TimeUnit.MILLISECONDS)) {
        		bot.kill();
        		throw new RuntimeException("Self not received by LogicModule after 10 secs, even though Self received by the bot!");
        	}
        }
        
		bot.pause();
			
		if (!bot.inState(IAgentStatePaused.class)) {
			throw new RuntimeException("Agent is not in paused state!");
		}
		
		bot.resume();
			
		if (!bot.inState(IAgentStateResumed.class)) {
			throw new RuntimeException("Agent is not in resumed state!");
		}
		
		final CountDownLatch commandSentLatch = new CountDownLatch(1); 
		CommandReact<TurnTo> turnToReactionOnce = new CommandReact<TurnTo>(TurnTo.class, bot.getAct()) {
			@Override
			protected void react(TurnTo event) {
				commandSentLatch.countDown();
			}
		};
		
		bot.getAct().act(new TurnTo().setRotation(new Rotation(-2000, -2000, -2000)));
        
       	try {
       		if (!commandSentLatch.await(1000, TimeUnit.MILLISECONDS)) {
				throw new RuntimeException("TurnTo command not sent or sensed.");
			}
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		
		switch (killMethod) {
		case 0:
			try {
				bot.stop();
			} catch (Exception e) {
				throw new RuntimeException("BOT FAILED TO bot.stop() !", e);
			}
			break;
		case 1:
			try {
				bot.kill();
			} catch (Exception e) {
				throw new RuntimeException("BOT FAILED TO bot.kill()!", e);
			}
			break;
		case 2:
			try {
				bot.getAct().act(new DisconnectBot());
			} catch (Exception e) {
				throw new RuntimeException("BOT FAILED TO bot.getAct().act(new DisconnectBot())!", e);
			}
			break;
		}
		
		if (bot.getState().getFlag() instanceof IAgentStateFailed) {
			throw new RuntimeException("Bot failed to stop due to previous exceptions or incorrent killing implementation. KILL METHOD: " + (killMethod == 0 ? "bot.stop()" : (killMethod == 1 ? "bot.kill()" : "bot.getAct().act(new DisconnectBot())")));
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
