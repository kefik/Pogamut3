package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test171_DM1on1Mixer_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Mixer";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test171_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Mixer.PathNode12, end: DM-1on1-Mixer.InventorySpot22 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Mixer.PathNode12",      "DM-1on1-Mixer.InventorySpot22",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test171_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Mixer.PathNode12, end: DM-1on1-Mixer.InventorySpot22 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Mixer.PathNode12",      "DM-1on1-Mixer.InventorySpot22",    20,                        true)
		);
	}

}
