package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test122_DMCurse4_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Curse4";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test122_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Curse4.PathNode90, end: DM-Curse4.PathNode61 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Curse4.PathNode90",      "DM-Curse4.PathNode61",    1,                        false)
		);
	}

        @Test
	public void test122_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Curse4.PathNode90, end: DM-Curse4.PathNode61 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Curse4.PathNode90",      "DM-Curse4.PathNode61",    20,                        false)
		);
	}

}
