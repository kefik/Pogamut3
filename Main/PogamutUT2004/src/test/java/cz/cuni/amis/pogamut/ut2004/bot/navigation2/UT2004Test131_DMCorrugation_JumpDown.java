package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test131_DMCorrugation_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Corrugation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test131_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Corrugation.PathNode61, end: DM-Corrugation.PathNode30 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Corrugation.PathNode61",      "DM-Corrugation.PathNode30",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test131_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Corrugation.PathNode61, end: DM-Corrugation.PathNode30 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Corrugation.PathNode61",      "DM-Corrugation.PathNode30",    20,                        false)
		);
	}

}
