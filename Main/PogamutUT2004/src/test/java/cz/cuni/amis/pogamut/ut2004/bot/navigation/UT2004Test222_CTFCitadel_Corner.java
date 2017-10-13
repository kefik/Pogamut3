package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test222_CTFCitadel_Corner extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test222_corner_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.InventorySpot180, end: CTF-Citadel.InventorySpot170 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot180",      "CTF-Citadel.InventorySpot170",    1,                        true)
		);
	}

        @Test
	public void test222_corner_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Citadel.InventorySpot180, end: CTF-Citadel.InventorySpot170 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.InventorySpot180",      "CTF-Citadel.InventorySpot170",    20,                        true)
		);
	}

}