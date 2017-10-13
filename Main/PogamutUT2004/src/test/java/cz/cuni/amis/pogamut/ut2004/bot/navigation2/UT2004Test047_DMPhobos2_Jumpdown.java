package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test047_DMPhobos2_Jumpdown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Phobos2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test47_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Phobos2.JumpSpot9, end: DM-Phobos2.JumpSpot8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Phobos2.JumpSpot9",      "DM-Phobos2.JumpSpot8",    1,                        false)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test47_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Phobos2.JumpSpot9, end: DM-Phobos2.JumpSpot8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Phobos2.JumpSpot9",      "DM-Phobos2.JumpSpot8",    20,                        false)
		);
	}

}
