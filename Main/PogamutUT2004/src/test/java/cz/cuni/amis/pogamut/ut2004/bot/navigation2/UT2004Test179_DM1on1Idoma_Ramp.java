package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test179_DM1on1Idoma_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Idoma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test179_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Idoma.PathNode36, end: DM-1on1-Idoma.PathNode34 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Idoma.PathNode36",      "DM-1on1-Idoma.PathNode34",    1,                        true)
		);
	}

        @Test
	public void test179_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: DM-1on1-Idoma.PathNode36, end: DM-1on1-Idoma.PathNode34 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Idoma.PathNode36",      "DM-1on1-Idoma.PathNode34",    20,                        true)
		);
	}

}
