package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test035_DMRankin_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rankin";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test35_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rankin.PathNode16, end: DM-Rankin.PlayerStart8 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PathNode16",      "DM-Rankin.PlayerStart8",    1,                        true)
		);
	}

        @Test
	public void test35_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Rankin.PathNode16, end: DM-Rankin.PlayerStart8 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PathNode16",      "DM-Rankin.PlayerStart8",    20,                        true)
		);
	}

}