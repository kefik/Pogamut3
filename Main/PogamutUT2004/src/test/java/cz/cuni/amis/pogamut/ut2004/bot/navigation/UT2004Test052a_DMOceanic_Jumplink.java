package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author knight
 */
public class UT2004Test052a_DMOceanic_Jumplink extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Oceanic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test52_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Oceanic.PathNode11, end: DM-Oceanic.JumpSpot34 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.JumpSpot34",      "DM-Oceanic.JumpSpot33",    1,                        false)
		);
	}

        @Test
	public void test52_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Oceanic.PathNode11, end: DM-Oceanic.JumpSpot34 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.JumpSpot34",      "DM-Oceanic.JumpSpot33",    20,                        false)
		);
	}

}