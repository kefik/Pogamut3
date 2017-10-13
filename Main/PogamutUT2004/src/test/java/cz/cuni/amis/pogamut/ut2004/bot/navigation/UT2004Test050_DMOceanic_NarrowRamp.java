package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test050_DMOceanic_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Oceanic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test50_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Oceanic.PathNode22, end: DM-Oceanic.PathNode36 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.PathNode22",      "DM-Oceanic.PathNode36",    1,                        true)
		);
	}

        @Test
	public void test50_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Oceanic.PathNode22, end: DM-Oceanic.PathNode36 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Oceanic.PathNode22",      "DM-Oceanic.PathNode36",    20,                        true)
		);
	}

}