package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test305_CTFMagma_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test305_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PathNode95, end: CTF-Magma.InventorySpot39 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Magma.PathNode95",      "CTF-Magma.InventorySpot39",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test305_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Magma.PathNode95, end: CTF-Magma.InventorySpot39 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Magma.PathNode95",      "CTF-Magma.InventorySpot39",    20,                        false)
		);
	}

}
