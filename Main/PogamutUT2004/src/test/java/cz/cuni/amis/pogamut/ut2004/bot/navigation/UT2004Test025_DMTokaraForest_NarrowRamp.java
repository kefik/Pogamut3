package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test025_DMTokaraForest_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-TokaraForest";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        /**
         * TODO: test fails
         */
        @Test
	public void test25_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-TokaraForest.PlayerStart16, end: DM-TokaraForest.PlayerStart18 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PlayerStart16",      "DM-TokaraForest.PlayerStart18",    1,                        true)
		);
	}

        /**
         *
         */
        @Test
	public void test25_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-TokaraForest.PlayerStart16, end: DM-TokaraForest.PlayerStart18 number of repetitions   both ways
			new NavigationTestBotParameters("DM-TokaraForest.PlayerStart16",      "DM-TokaraForest.PlayerStart18",    20,                        true)
		);
	} 

}
