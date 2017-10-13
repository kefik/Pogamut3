package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test207_CTFBridgeOfFate_Corner extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test207_corner_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.JumpSpot3, end: CTF-BridgeOfFate.PathNode214 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.JumpSpot3",      "CTF-BridgeOfFate.PathNode214",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test207_corner_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-BridgeOfFate.JumpSpot3, end: CTF-BridgeOfFate.PathNode214 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.JumpSpot3",      "CTF-BridgeOfFate.PathNode214",    20,                        true)
		);
	}

}
