package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test320_CTFMoonDragon_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test320_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.InventorySpot152, end: CTF-MoonDragon.PathNode132 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.InventorySpot152",      "CTF-MoonDragon.PathNode132",    1,                        false)
		);
	}

        @Test
	public void test320_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-MoonDragon.InventorySpot152, end: CTF-MoonDragon.PathNode132 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.InventorySpot152",      "CTF-MoonDragon.PathNode132",    20,                        false)
		);
	}

}