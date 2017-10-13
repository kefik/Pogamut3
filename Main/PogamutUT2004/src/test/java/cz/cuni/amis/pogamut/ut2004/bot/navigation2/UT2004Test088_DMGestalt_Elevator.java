package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test088_DMGestalt_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test88_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.LiftExit1, end: DM-Gestalt.PathNode66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gestalt.LiftExit1",      "DM-Gestalt.PathNode66",    1,                        true)
		);
	}

        @Test
	public void test88_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Gestalt.LiftExit1, end: DM-Gestalt.PathNode66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gestalt.LiftExit1",      "DM-Gestalt.PathNode66",    20,                        true)
		);
	}

}
