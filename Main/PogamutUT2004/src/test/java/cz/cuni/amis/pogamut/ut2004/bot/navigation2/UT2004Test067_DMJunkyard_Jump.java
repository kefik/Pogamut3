package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test067_DMJunkyard_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Junkyard";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test67_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Junkyard.PathNode155, end: DM-Junkyard.JumpSpot0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.PathNode155",      "DM-Junkyard.JumpSpot0",    1,                        false)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test67_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-Junkyard.PathNode155, end: DM-Junkyard.JumpSpot0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.PathNode155",      "DM-Junkyard.JumpSpot0",    20,                        false)
		);
	}

}
