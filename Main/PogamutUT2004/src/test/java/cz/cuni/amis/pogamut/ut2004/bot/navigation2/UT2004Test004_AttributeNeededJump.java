package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Knight
 */
public class UT2004Test004_AttributeNeededJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Corrugation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testAttributeNeededJump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Corrugation.InventorySpot95, end: DM-Corrugation.JumpSpot0 number of repetitions
			new Navigation2TestBotParameters("DM-Corrugation.PathNode11", "DM-Corrugation.JumpSpot0",1)
		);
	}

	@Test
	public void testAttributeNeededJump_20_times() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Corrugation.InventorySpot95, end: DM-Corrugation.JumpSpot0 number of repetitions
			new Navigation2TestBotParameters("DM-Corrugation.PathNode11", "DM-Corrugation.JumpSpot0", 20)
		);
	}
}
