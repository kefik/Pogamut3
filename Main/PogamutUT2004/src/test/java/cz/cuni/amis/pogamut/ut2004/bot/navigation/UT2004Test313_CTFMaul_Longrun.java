package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test313_CTFMaul_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Maul";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test313_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Maul.PathNode13, end: CTF-Maul.InventorySpot807 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode13",      "CTF-Maul.InventorySpot807",    1,                        true)
		);
	}

        @Test
	public void test313_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Maul.PathNode13, end: CTF-Maul.InventorySpot807 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode13",      "CTF-Maul.InventorySpot807",    20,                        true)
		);
	}

}