package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test018_DMGoliath_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test18_jumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.InventorySpot172, end: DM-Goliath.JumpSpot15 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.InventorySpot172",      "DM-Goliath.JumpSpot15",    1,                        false)
		);
	}

        /**
         * TODO: Test fails
         */
        @Test
	public void test18_jumps_20_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Goliath.InventorySpot172, end: DM-Goliath.JumpSpot15 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.InventorySpot172",      "DM-Goliath.JumpSpot15",    20,                        false)
		);
	}

}
