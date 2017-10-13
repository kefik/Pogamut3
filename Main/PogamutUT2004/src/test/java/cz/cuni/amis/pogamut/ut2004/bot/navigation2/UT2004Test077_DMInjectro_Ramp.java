package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test077_DMInjectro_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Injector";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test77_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Injector.PathNode51, end: DM-Injector.PathNode41 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Injector.PathNode78",      "DM-Injector.PathNode80",    1,                        true)
		);
	}

        @Test
	public void test77_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Injector.PathNode51, end: DM-Injector.PathNode41 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Injector.PathNode51",      "DM-Injector.PathNode41",    20,                        true)
		);
	}

}
