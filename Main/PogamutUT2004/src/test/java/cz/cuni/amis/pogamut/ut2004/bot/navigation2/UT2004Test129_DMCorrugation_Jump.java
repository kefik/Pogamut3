package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test129_DMCorrugation_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Corrugation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test129_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Corrugation.PathNode16, end: DM-Corrugation.PathNode85 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Corrugation.PathNode16",      "DM-Corrugation.PathNode85",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test129_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Corrugation.PathNode16, end: DM-Corrugation.PathNode85 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Corrugation.PathNode16",      "DM-Corrugation.PathNode85",    20,                        true)
		);
	}

}
