package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test064_DMJunkyard_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Junkyard";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test64_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Junkyard.PathNode254, end: DM-Junkyard.PathNode262 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.PathNode254",      "DM-Junkyard.PathNode262",    1,                        true)
		);
	}

        @Test
	public void test64_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Junkyard.PathNode254, end: DM-Junkyard.PathNode262 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.PathNode254",      "DM-Junkyard.PathNode262",    20,                        true)
		);
	}

}
