package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test178_DM1on1Idoma_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Idoma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test178_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Idoma.InventorySpot1, end: DM-1on1-Idoma.PathNode31 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Idoma.InventorySpot1",      "DM-1on1-Idoma.PathNode31",    1,                        false)
		);
	}

        @Test
	public void test178_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-1on1-Idoma.InventorySpot1, end: DM-1on1-Idoma.PathNode31 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Idoma.InventorySpot1",      "DM-1on1-Idoma.PathNode31",    20,                        false)
		);
	}

}