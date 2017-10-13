package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test093_DMGestalt_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test93_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.PathNode45, end: DM-Gestalt.PathNode4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.PathNode45",      "DM-Gestalt.PathNode4",    1,                        true)
		);
	}

        @Test
	public void test93_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Gestalt.PathNode45, end: DM-Gestalt.PathNode4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.PathNode45",      "DM-Gestalt.PathNode4",    20,                        true)
		);
	}

}