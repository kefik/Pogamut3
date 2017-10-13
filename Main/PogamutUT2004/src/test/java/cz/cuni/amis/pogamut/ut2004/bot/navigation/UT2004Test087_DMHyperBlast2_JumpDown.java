package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test087_DMHyperBlast2_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-HyperBlast2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test87_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-HyperBlast2.PathNode2, end: DM-HyperBlast2.InventorySpot4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.PathNode2",      "DM-HyperBlast2.InventorySpot4",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test87_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: DM-HyperBlast2.PathNode2, end: DM-HyperBlast2.InventorySpot4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.PathNode2",      "DM-HyperBlast2.InventorySpot4",    20,                        false)
		);
	}

}