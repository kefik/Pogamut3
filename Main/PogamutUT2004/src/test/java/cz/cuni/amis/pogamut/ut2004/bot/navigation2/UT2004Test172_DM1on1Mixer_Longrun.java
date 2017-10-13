package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test172_DM1on1Mixer_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Mixer";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test172_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Mixer.InventorySpot4, end: DM-1on1-Mixer.PlayerStart0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Mixer.InventorySpot4",      "DM-1on1-Mixer.PlayerStart0",    1,                        true)
		);
	}

        @Test
	public void test172_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Mixer.InventorySpot4, end: DM-1on1-Mixer.PlayerStart0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Mixer.InventorySpot4",      "DM-1on1-Mixer.PlayerStart0",    20,                        true)
		);
	}

}
