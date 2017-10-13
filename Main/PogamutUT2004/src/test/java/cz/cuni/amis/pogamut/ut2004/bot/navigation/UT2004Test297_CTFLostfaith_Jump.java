package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test297_CTFLostfaith_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Lostfaith";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test297_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Lostfaith.InventorySpot104, end: CTF-Lostfaith.InventorySpot119 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.InventorySpot104",      "CTF-Lostfaith.InventorySpot119",    1,                        false)
		);
	}

        @Test
	public void test297_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Lostfaith.InventorySpot104, end: CTF-Lostfaith.InventorySpot119 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.InventorySpot104",      "CTF-Lostfaith.InventorySpot119",    20,                        false)
		);
	}

}