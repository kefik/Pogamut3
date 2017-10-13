package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test221_CTFCitadel_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test221_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.InventorySpot178, end: CTF-Citadel.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot178",      "CTF-Citadel.xRedFlagBase0",    1,                        false)
		);
	}

        @Test
	public void test221_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Citadel.InventorySpot178, end: CTF-Citadel.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot178",      "CTF-Citadel.xRedFlagBase0",    20,                        false)
		);
	}

}