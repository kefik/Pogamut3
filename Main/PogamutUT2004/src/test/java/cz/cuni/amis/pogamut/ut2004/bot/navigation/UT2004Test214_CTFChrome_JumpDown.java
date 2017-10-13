package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test214_CTFChrome_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Chrome";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test214_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Chrome.JumpSpot9, end: CTF-Chrome.InventorySpot20 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.JumpSpot9",      "CTF-Chrome.InventorySpot20",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test214_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Chrome.JumpSpot9, end: CTF-Chrome.InventorySpot20 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.JumpSpot9",      "CTF-Chrome.InventorySpot20",    20,                        false)
		);
	}

}