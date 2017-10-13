package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test208_CTFBridgeOfFate_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-BridgeOfFate";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test208_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-BridgeOfFate.InventorySpot38, end: CTF-BridgeOfFate.InventorySpot62 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.InventorySpot38",      "CTF-BridgeOfFate.InventorySpot62",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test208_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-BridgeOfFate.InventorySpot38, end: CTF-BridgeOfFate.InventorySpot62 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-BridgeOfFate.InventorySpot38",      "CTF-BridgeOfFate.InventorySpot62",    20,                        false)
		);
	}

}