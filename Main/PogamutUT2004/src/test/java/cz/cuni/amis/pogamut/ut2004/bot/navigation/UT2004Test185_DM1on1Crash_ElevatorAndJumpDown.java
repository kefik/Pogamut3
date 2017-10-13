package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test185_DM1on1Crash_ElevatorAndJumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Crash";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test185_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Crash.InventorySpot1, end: DM-1on1-Crash.InvantorySpot53 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Crash.InventorySpot1",      "DM-1on1-Crash.InventorySpot53",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test185_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-1on1-Crash.InventorySpot1, end: DM-1on1-Crash.InvantorySpot53 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Crash.InventorySpot1",      "DM-1on1-Crash.InventorySpot53",    20,                        true)
		);
	}

}