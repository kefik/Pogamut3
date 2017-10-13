package cz.cuni.amis.pogamut.ut2004.bot.killbot;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.flag.FlagListener;

public class UT2004Test01_BotDeadState extends UT2004Test {
	
	@Override
	public void startUCC(UCCWrapperConf uccConf) throws UCCStartException {
		uccConf.setMapName("DM-TrainingDay");
		uccConf.setStartOnUnusedPort(false);
		super.startUCC(uccConf);
	}
	
	@Test
	public void test() {
		UT2004BotRunner<UT2004Bot, KillBotParameters> killBotRunner = new UT2004BotRunner<UT2004Bot, KillBotParameters>(
			new UT2004BotFactory<UT2004Bot, KillBotParameters>(
				new UT2004BotModule<KillBotParameters>(KillBotController.class)
			)
		);
		List<UT2004Bot> bots = killBotRunner.startAgents(
			 new KillBotParameters(new Location(1760, -600, -70), new Location(2000, -700, -70))
			,new KillBotParameters(new Location(2000, -700, -70), new Location(1760, -600, -70))
			,new KillBotParameters(new Location(1950, -460, -70), new Location(1876, -800, -70))
			,new KillBotParameters(new Location(1876, -800, -70), new Location(1950, -460, -70))
		);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final int desiredKillCount = 50;
		
		FlagListener<Integer> listener = new FlagListener<Integer>() {

			@Override
			public synchronized void flagChanged(Integer changedValue) {
				if (changedValue >= desiredKillCount) {
					latch.countDown();
				}
			}
			
		};
		
		((KillBotController)bots.get(0).getController()).getKilled().addListener(listener);
		((KillBotController)bots.get(1).getController()).getKilled().addListener(listener);
		((KillBotController)bots.get(2).getController()).getKilled().addListener(listener);
		((KillBotController)bots.get(3).getController()).getKilled().addListener(listener);
		
		try {
			latch.await(desiredKillCount * 20 * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Assert.fail("Interrupted on the latch");
		}
		
		try {
			bots.get(0).stop();
		} finally {
			try {
				bots.get(1).stop();
			} finally {
				try {
					bots.get(2).stop();
				} finally {
					try {
						bots.get(3).stop();
					} finally {
						System.out.println("Bot 1 deaths: " + ((KillBotController)bots.get(0).getController()).getKilled().getFlag());
						System.out.println("Bot 2 deaths: " + ((KillBotController)bots.get(1).getController()).getKilled().getFlag());
						System.out.println("Bot 3 deaths: " + ((KillBotController)bots.get(2).getController()).getKilled().getFlag());
						System.out.println("Bot 4 deaths: " + ((KillBotController)bots.get(3).getController()).getKilled().getFlag());
						System.out.println("Total deaths: " + (((KillBotController)bots.get(0).getController()).getKilled().getFlag() + ((KillBotController)bots.get(1).getController()).getKilled().getFlag() + ((KillBotController)bots.get(2).getController()).getKilled().getFlag() + ((KillBotController)bots.get(3).getController()).getKilled().getFlag()));
						System.out.println("Desired deaths count (for one bot): " + desiredKillCount);
						
						Assert.assertTrue("Test failed, desired kill count not reached...",
							((KillBotController)bots.get(0).getController()).getKilled().getFlag() >= desiredKillCount ||
							((KillBotController)bots.get(1).getController()).getKilled().getFlag() >= desiredKillCount ||
							((KillBotController)bots.get(2).getController()).getKilled().getFlag() >= desiredKillCount ||
							((KillBotController)bots.get(3).getController()).getKilled().getFlag() >= desiredKillCount
						);

					}
				}
			}
		}
		
		System.out.println("---/// TEST OK ///---");
	}

}
