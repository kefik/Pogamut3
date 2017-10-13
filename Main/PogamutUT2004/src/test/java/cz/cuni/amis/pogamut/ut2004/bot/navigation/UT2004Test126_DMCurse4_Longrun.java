package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test126_DMCurse4_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Curse4";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test126_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Curse4.PlayerStart16, end: DM-Curse4.PlayerStart12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart16",      "DM-Curse4.PlayerStart12",    1,                        true)
		);
	}

        @Test
	public void test126_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 15 minutes
			15,
			// test movement between        start: DM-Curse4.PlayerStart16, end: DM-Curse4.PlayerStart12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart16",      "DM-Curse4.PlayerStart12",    20,                        true)
		);
	}

}