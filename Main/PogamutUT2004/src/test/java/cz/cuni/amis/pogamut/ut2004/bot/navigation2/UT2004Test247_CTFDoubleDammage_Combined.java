package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test247_CTFDoubleDammage_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DoubleDammage";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test247_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-DoubleDammage.InventorySpot278, end: CTF-DoubleDammage.JumpSpot14 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DoubleDammage.InventorySpot278",      "CTF-DoubleDammage.JumpSpot11",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test247_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 15 minutes
			15,
			// test movement between        start: CTF-DoubleDammage.InventorySpot278, end: CTF-DoubleDammage.JumpSpot14 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DoubleDammage.InventorySpot278",      "CTF-DoubleDammage.JumpSpot11",    20,                        true)
		);
	}

}
