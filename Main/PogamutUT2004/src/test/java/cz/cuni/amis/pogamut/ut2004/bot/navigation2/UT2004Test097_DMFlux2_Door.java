package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test097_DMFlux2_Door extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Flux2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test97_doors_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Flux2.PathNode95, end: DM-Flux2.PathNode25 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Flux2.PathNode95",      "DM-Flux2.PathNode25",    1,                        true)
		);
	}

        @Test
	public void test97_doors_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Flux2.PathNode95, end: DM-Flux2.PathNode25 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Flux2.PathNode95",      "DM-Flux2.PathNode25",    20,                        true)
		);
	}

}
