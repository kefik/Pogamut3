package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test017_DMGoliath_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test17_jumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.InventorySpot175, end: DM-Goliath.JumpSpot17 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Goliath.InventorySpot175",      "DM-Goliath.PathNode95",    1,                        false)
		);
	}

         /**
         * TODO: Test fails
         */
        @Test
	public void test17_jumps_20_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Goliath.InventorySpot175, end: DM-Goliath.JumpSpot17 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Goliath.InventorySpot175",      "DM-Goliath.PathNode95",    20,                        false)
		);
	}

}
