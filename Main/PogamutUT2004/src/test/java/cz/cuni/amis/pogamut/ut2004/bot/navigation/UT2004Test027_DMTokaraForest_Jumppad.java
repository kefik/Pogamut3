package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test027_DMTokaraForest_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-TokaraForest";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test27_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-TokaraForest.PathNode22, end: DM-TokaraForest.PathNode212 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PathNode22",      "DM-TokaraForest.PathNode212",    1,                        false)
		);
	}

        @Test
	public void test27_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-TokaraForest.PathNode22, end: DM-TokaraForest.PathNode212 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PathNode22",      "DM-TokaraForest.PathNode212",    20,                        false)
		);
	}

}
