package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test086_DMHyperBlast2_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-HyperBlast2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test86_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-HyperBlast2.PathNode19, end: DM-HyperBlast2.JumpSpot11 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-HyperBlast2.PathNode19",      "DM-HyperBlast2.JumpSpot11",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test86_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-HyperBlast2.PathNode19, end: DM-HyperBlast2.JumpSpot11 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-HyperBlast2.PathNode19",      "DM-HyperBlast2.JumpSpot11",    20,                        true)
		);
	}

}
