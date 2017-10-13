package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test051_DMOceanic_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Oceanic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test51_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Oceanic.PlayerStart3, end: DM-Oceanic.PathNode41 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.PlayerStart3",      "DM-Oceanic.PathNode41",    1,                        true)
		);
	}

        @Test
	public void test51_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Oceanic.PlayerStart3, end: DM-Oceanic.PathNode41 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.PlayerStart3",      "DM-Oceanic.PathNode41",    20,                        true)
		);
	}

}