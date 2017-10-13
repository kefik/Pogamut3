package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test206_CTFBridgeOfFate_Door extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test206_door_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.PathNode397, end: CTF-BridgeOfFate.JumpSpot17 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.PathNode397",      "CTF-BridgeOfFate.JumpSpot17",    1,                        true)
		);
	}

        @Test
	public void test206_door_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-BridgeOfFate.PathNode397, end: CTF-BridgeOfFate.JumpSpot17 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.PathNode397",      "CTF-BridgeOfFate.JumpSpot17",    20,                        true)
		);
	}

}