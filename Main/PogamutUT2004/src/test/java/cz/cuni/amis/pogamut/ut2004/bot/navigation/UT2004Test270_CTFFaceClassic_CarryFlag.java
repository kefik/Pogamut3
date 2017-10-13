package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test270_CTFFaceClassic_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-FaceClassic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test270_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-FaceClassic.xRedFlagBase1, end: CTF-FaceClassic.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-FaceClassic.xRedFlagBase1",      "CTF-FaceClassic.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test270_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 25 minutes
			25,
			// test movement between        start: CTF-FaceClassic.xRedFlagBase1, end: CTF-FaceClassic.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-FaceClassic.xRedFlagBase1",      "CTF-FaceClassic.xBlueFlagBase0",    20,                        false)
		);
	}

}