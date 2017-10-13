package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test137_DMAsbestos_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Asbestos";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test137_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Asbestos.PathNode179, end: DM-Asbestos.InventorySpot22 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Asbestos.PathNode179",      "DM-Asbestos.InventorySpot22",    1,                        true)
		);
	}

        @Test
	public void test137_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Asbestos.PathNode179, end: DM-Asbestos.InventorySpot22 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Asbestos.PathNode179",      "DM-Asbestos.InventorySpot22",    20,                        true)
		);
	}

}