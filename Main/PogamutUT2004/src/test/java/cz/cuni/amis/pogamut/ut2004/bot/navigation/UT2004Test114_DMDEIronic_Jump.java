package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test114_DMDEIronic_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Ironic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test114_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Ironic.InventorySpot114, end: DM-DE-Ironic.PathNode47 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.InventorySpot114",      "DM-DE-Ironic.PathNode47",    1,                        false)
		);
	}

        @Test
	public void test114_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Ironic.InventorySpot114, end: DM-DE-Ironic.PathNode47 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.InventorySpot114",      "DM-DE-Ironic.PathNode47",    20,                        false)
		);
	}

}