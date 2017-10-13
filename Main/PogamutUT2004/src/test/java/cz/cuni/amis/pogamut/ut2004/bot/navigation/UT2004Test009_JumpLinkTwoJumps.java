package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * Quite ok.
 *
 * @author Knight
 */
public class UT2004Test009_JumpLinkTwoJumps extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Asbestos";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testJumpLinkTwoJumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new NavigationTestBotParameters("DM-Asbestos.InventorySpot47", "DM-Asbestos.PathNode160",1)
		);
	}

	@Test
	public void testJumpLinkTwoJumps_20_times() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new NavigationTestBotParameters("DM-Asbestos.InventorySpot47", "DM-Asbestos.PathNode160", 20)
		);
	}
}
