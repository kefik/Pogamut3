package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test218_CTFCitadel_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test218_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.InventorySpot157, end: CTF-Citadel.InventorySpot182 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot157",      "CTF-Citadel.InventorySpot182",    1,                        false)
		);
	}

        @Test
	public void test218_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-Citadel.InventorySpot157, end: CTF-Citadel.InventorySpot182 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot157",      "CTF-Citadel.InventorySpot182",    20,                        false)
		);
	}

}