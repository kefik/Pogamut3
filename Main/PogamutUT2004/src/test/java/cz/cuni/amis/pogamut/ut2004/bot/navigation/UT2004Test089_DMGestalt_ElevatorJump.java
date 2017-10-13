package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test089_DMGestalt_ElevatorJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test89_elevatorjump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.LiftExit1, end: DM-Gestalt.LiftExit7 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.LiftExit1",      "DM-Gestalt.LiftExit7",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        * notice: this is VERY difficult test - bot would have to jump at the precise moment when the elevator stops
        */
        @Test
	public void test89_elevatorjump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-Gestalt.LiftExit1, end: DM-Gestalt.LiftExit7 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.LiftExit1",      "DM-Gestalt.LiftExit7",    20,                        false)
		);
	}

}