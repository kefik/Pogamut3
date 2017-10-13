package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test182_DM1on1Desolation_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Desolation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test182_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Desolation.PathNode48, end: DM-1on1-Desolation.InventorySpot53 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Desolation.PathNode48",      "DM-1on1-Desolation.InventorySpot53",    1,                        false)
		);
	}

        @Test
	public void test182_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: DM-1on1-Desolation.PathNode48, end: DM-1on1-Desolation.InventorySpot53 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Desolation.PathNode48",      "DM-1on1-Desolation.InventorySpot53",    20,                        false)
		);
	}

}
