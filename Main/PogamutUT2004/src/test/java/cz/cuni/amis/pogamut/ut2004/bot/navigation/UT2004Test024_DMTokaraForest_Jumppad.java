package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test024_DMTokaraForest_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-TokaraForest";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test24_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-TokaraForest.PlayerStart12, end: DM-TokaraForest.PathNode64 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PlayerStart12",      "DM-TokaraForest.PathNode64",    1,                        false)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test24_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-TokaraForest.PlayerStart12, end: DM-TokaraForest.PathNode64 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PlayerStart12",      "DM-TokaraForest.PathNode64",    20,                        false)
		); 
	}

}
