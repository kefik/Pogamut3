package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test304_CTFMagma_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test304_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.InventorySpot27, end: CTF-Magma.PathNode77 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Magma.InventorySpot27",      "CTF-Magma.PathNode77",    1,                        false)
		);
	}

        @Test
	public void test304_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Magma.InventorySpot27, end: CTF-Magma.PathNode77 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Magma.InventorySpot27",      "CTF-Magma.PathNode77",    20,                        false)
		);
	}

}
