package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test166_DM1on1Roughinery_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Roughinery";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test166_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Roughinery.PathNode87, end: DM-1on1-Roughinery.LiftExit0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Roughinery.PathNode87",      "DM-1on1-Roughinery.LiftExit0",    1,                        false)
		);
	}

        @Test
	public void test166_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-1on1-Roughinery.PathNode87, end: DM-1on1-Roughinery.LiftExit0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Roughinery.PathNode87",      "DM-1on1-Roughinery.LiftExit0",    20,                        false)
		);
	}

}
