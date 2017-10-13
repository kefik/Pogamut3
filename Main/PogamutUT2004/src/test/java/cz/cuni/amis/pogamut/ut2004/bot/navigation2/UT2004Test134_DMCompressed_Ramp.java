package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test134_DMCompressed_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Compressed";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test134_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Compressed.JumpSpot3, end: DM-Compressed.PathNode6 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Compressed.JumpSpot3",      "DM-Compressed.PathNode6",    1,                        true)
		);
	}

        @Test
	public void test134_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Compressed.JumpSpot3, end: DM-Compressed.PathNode6 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Compressed.JumpSpot3",      "DM-Compressed.PathNode6",    20,                        true)
		);
	}

}
