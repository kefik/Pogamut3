package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * Manages to jump very rarely.. :-/
 * 
 * @author Knight
 */
public class UT2004Test008_JumpLinkFour extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Antalus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testJumpLinkFour_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new Navigation2TestBotParameters("DM-Antalus.PathNode32", "DM-Antalus.JumpSpot14",1)
		);
	}

	@Test
	public void testJumpLinkFour_20_times() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new Navigation2TestBotParameters("DM-Antalus.PathNode32", "DM-Antalus.JumpSpot14", 20)
		);
	}
}
