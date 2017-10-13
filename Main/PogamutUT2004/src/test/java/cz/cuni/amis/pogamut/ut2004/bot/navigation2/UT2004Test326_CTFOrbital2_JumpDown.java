package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test326_CTFOrbital2_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Orbital2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test326_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Orbital2.InventorySpot207, end: CTF-Orbital2.PathNode243 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Orbital2.InventorySpot207",      "CTF-Orbital2.PathNode243",    1,                        false)
		);
	}

        @Test
	public void test326_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Orbital2.InventorySpot207, end: CTF-Orbital2.PathNode243 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Orbital2.InventorySpot207",      "CTF-Orbital2.PathNode243",    20,                        false)
		);
	}

}
