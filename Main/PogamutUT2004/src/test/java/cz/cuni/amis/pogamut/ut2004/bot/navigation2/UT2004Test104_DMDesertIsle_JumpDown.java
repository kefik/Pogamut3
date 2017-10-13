package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test104_DMDesertIsle_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DesertIsle";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test104_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DesertIsle.JumpSpot31, end: DM-DesertIsle.JumpSpot66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DesertIsle.JumpSpot31",      "DM-DesertIsle.JumpSpot66",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test104_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DesertIsle.JumpSpot31, end: DM-DesertIsle.JumpSpot66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DesertIsle.JumpSpot31",      "DM-DesertIsle.JumpSpot66",    20,                        false)
		);
	}

}
