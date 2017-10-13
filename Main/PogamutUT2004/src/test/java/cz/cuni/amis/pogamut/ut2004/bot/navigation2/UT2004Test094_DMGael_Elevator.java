package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test094_DMGael_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gael";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test94_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gael.PlayerStart5, end: DM-Gael.PlayerStart8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gael.PlayerStart5",      "DM-Gael.PlayerStart8",    1,                        true)
		);
	}

        @Test
	public void test94_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Gael.PlayerStart5, end: DM-Gael.PlayerStart8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gael.PlayerStart5",      "DM-Gael.PlayerStart8",    20,                        true)
		);
	}

}
