package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test209_CTFBridgeOfFate_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test209_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.PathNode302, end: CTF-BridgeOfFate.PathNode13 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.PathNode302",      "CTF-BridgeOfFate.PathNode13",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test209_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-BridgeOfFate.PathNode302, end: CTF-BridgeOfFate.PathNode13 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.PathNode302",      "CTF-BridgeOfFate.PathNode13",    20,                        true)
		);
	}

}
