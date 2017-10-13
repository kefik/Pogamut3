package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test136_DMCompressed_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Compressed";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test136_doublejump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Compressed.PathNode8, end: DM-Compressed.JumpSpot14 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Compressed.PathNode8",      "DM-Compressed.JumpSpot14",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test136_doublejump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Compressed.PathNode8, end: DM-Compressed.JumpSpot14 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Compressed.PathNode8",      "DM-Compressed.JumpSpot14",    20,                        true)
		);
	}

}