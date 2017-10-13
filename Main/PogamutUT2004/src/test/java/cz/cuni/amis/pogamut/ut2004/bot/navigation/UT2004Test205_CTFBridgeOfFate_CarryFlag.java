package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test205_CTFBridgeOfFate_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test205_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.xRedFlagBase1, end: CTF-BridgeOfFate.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.xRedFlagBase1",      "CTF-BridgeOfFate.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test205_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 25 minutes
			25,
			// test movement between        start: CTF-BridgeOfFate.xRedFlagBase1, end: CTF-BridgeOfFate.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.xRedFlagBase1",      "CTF-BridgeOfFate.xBlueFlagBase1",    20,                        false)
		);
	}

}