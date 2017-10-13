package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test210_CTFBridgeOfFate_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test210_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.PathNode66, end: CTF-BridgeOfFate.JumpSpot11 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.PathNode66",      "CTF-BridgeOfFate.JumpSpot11",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test210_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-BridgeOfFate.PathNode66, end: CTF-BridgeOfFate.JumpSpot11 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.PathNode66",      "CTF-BridgeOfFate.JumpSpot11",    20,                        true)
		);
	}

}