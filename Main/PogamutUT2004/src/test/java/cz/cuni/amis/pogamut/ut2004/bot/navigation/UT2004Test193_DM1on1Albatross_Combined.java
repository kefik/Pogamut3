package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * 
 * @author Peta Michalik
 */
public class UT2004Test193_DM1on1Albatross_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Albatross";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	/**
	 * TODO: test fails!
	 */
	@Test
	public void test193_combined_1_time() {
		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 1 minute
				1,
				// test movement between start: DM-1on1-Albatross.AIMarker8,
				// end: DM-1on1-Albatross.LiftExit3 number of repetitions both
				// ways
				new NavigationTestBotParameters("DM-1on1-Albatross.AIMarker8",
						"DM-1on1-Albatross.LiftExit3", 1, true));
	}

	/*
	 * TODO: Test fails
	 */
	@Test
	public void test193_combined_20_time() {

		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 10 minutes
				10,
				// test movement between start: DM-1on1-Albatross.AIMarker8,
				// end: DM-1on1-Albatross.LiftExit3 number of repetitions both
				// ways
				new NavigationTestBotParameters("DM-1on1-Albatross.AIMarker8",
						"DM-1on1-Albatross.LiftExit3", 20, true));
	}

}