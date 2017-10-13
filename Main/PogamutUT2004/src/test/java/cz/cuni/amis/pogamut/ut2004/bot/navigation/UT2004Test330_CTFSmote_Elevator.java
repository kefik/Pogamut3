package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test330_CTFSmote_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Smote";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test330_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Smote.PlayerStart44, end: CTF-Smote.InventorySpot159 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Smote.PlayerStart44",      "CTF-Smote.InventorySpot159",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test330_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 7 minutes
			7,
			// test movement between        start: CTF-Smote.PlayerStart44, end: CTF-Smote.InventorySpot159 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Smote.PlayerStart44",      "CTF-Smote.InventorySpot159",    20,                        true)
		);
	}

}