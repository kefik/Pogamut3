package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test332_CTFSmote_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Smote";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test332_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Smote.JumpSpot13, end: CTF-Smote.InventorySpot183 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.JumpSpot13",      "CTF-Smote.InventorySpot183",    1,                        true)
		);
	}

        @Test
	public void test332_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Smote.JumpSpot13, end: CTF-Smote.InventorySpot183 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.JumpSpot13",      "CTF-Smote.InventorySpot183",    20,                        true)
		);
	}

}
