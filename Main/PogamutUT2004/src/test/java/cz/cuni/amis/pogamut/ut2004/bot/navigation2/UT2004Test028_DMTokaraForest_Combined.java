package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test028_DMTokaraForest_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-TokaraForest";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test28_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-TokaraForest.PlayerStart18, end: DM-TokaraForest.PathNode99 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-TokaraForest.PlayerStart18",      "DM-TokaraForest.PathNode99",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test28_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 12 minutes
			12,
			// test movement between        start: DM-TokaraForest.PlayerStart18, end: DM-TokaraForest.PathNode99 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-TokaraForest.PlayerStart18",      "DM-TokaraForest.PathNode99",    20,                        true)
		);
	}

}
