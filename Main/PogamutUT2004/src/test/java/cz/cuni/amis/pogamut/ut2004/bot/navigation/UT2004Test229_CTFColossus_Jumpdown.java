package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test229_CTFColossus_Jumpdown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Colossus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test229_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Colossus.PathNode386, end: CTF-Colossus.InventorySpot121 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.PathNode386",      "CTF-Colossus.InventorySpot121",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test229_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Colossus.PathNode386, end: CTF-Colossus.InventorySpot121 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.PathNode386",      "CTF-Colossus.InventorySpot121",    20,                        false)
		);
	}

}