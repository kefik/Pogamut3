package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test149_DM1on1Trite_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Trite";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test149_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Trite.InventorySpot70, end: DM-1on1-Trite.PlayerStart7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Trite.InventorySpot70",      "DM-1on1-Trite.PlayerStart7",    1,                        false)
		);
	}

        @Test
	public void test149_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-1on1-Trite.InventorySpot70, end: DM-1on1-Trite.PlayerStart7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Trite.InventorySpot70",      "DM-1on1-Trite.PlayerStart7",    20,                        false)
		);
	}

}
