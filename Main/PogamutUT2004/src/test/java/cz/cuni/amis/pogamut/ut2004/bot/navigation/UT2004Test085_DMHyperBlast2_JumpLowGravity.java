package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test085_DMHyperBlast2_JumpLowGravity extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-HyperBlast2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test85_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-HyperBlast2.InventorySpot58, end: DM-HyperBlast2.InventorySpot1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.InventorySpot58",      "DM-HyperBlast2.InventorySpot1",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test85_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-HyperBlast2.InventorySpot58, end: DM-HyperBlast2.InventorySpot1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.InventorySpot58",      "DM-HyperBlast2.InventorySpot1",    20,                        true)
		);
	}

}