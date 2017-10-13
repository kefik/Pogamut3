package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test111_DMDEOsiris2_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Osiris2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test111_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Osiris2.PathNode20, end: DM-DE-Osiris2.PathNode21 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Osiris2.PathNode20",      "DM-DE-Osiris2.PathNode21",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test111_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Osiris2.PathNode20, end: DM-DE-Osiris2.PathNode21 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Osiris2.PathNode20",      "DM-DE-Osiris2.PathNode21",    20,                        true)
		);
	}

}
