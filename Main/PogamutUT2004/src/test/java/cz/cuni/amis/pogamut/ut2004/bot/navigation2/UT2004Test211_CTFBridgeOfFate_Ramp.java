package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test211_CTFBridgeOfFate_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test211_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.PathNode478, end: CTF-BridgeOfFate.xRedFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.PathNode478",      "CTF-BridgeOfFate.xRedFlagBase1",    1,                        true)
		);
	}

        @Test
	public void test211_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-BridgeOfFate.PathNode478, end: CTF-BridgeOfFate.xRedFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-BridgeOfFate.PathNode478",      "CTF-BridgeOfFate.xRedFlagBase1",    20,                        true)
		);
	}

}
