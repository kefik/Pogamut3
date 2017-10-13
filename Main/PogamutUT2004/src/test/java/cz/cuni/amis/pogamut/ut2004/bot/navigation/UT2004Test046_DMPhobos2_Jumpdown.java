package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test046_DMPhobos2_Jumpdown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Phobos2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test46_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Phobos2.PathNode113, end: DM-Phobos2.PathNode111 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode113",      "DM-Phobos2.PathNode111",    1,                        false)
		);
	}

        @Test
	public void test46_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Phobos2.PathNode113, end: DM-Phobos2.PathNode111 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode113",      "DM-Phobos2.PathNode111",    20,                        false)
		);
	}

}