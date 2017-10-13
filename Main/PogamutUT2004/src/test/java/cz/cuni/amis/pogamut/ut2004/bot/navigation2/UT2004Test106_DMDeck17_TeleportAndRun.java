package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test106_DMDeck17_TeleportAndRun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Deck17";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test106_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Deck17.PathNode672, end: DM-Deck17.PathNode695 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Deck17.PathNode672",      "DM-Deck17.PathNode695",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test106_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Deck17.PathNode672, end: DM-Deck17.PathNode695 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Deck17.PathNode672",      "DM-Deck17.PathNode695",    20,                        true)
		);
	}

}
