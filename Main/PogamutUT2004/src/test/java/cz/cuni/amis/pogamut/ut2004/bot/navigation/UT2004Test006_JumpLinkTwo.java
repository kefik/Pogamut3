package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Knight
 */
public class UT2004Test006_JumpLinkTwo extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Corrugation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testJumpLinkTwo_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new NavigationTestBotParameters("DM-Corrugation.LiftExit1", "DM-Corrugation.PathNode16",1)
		);
	}

	@Test
	public void testJumpLinkTwo_20_times() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new NavigationTestBotParameters("DM-Corrugation.LiftExit1", "DM-Corrugation.PathNode16", 20)
		);
	}
}
