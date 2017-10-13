package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test276_CTFGeothermal_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Geothermal";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test276_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Geothermal.PathNode19, end: CTF-Geothermal.JumpSpot17 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Geothermal.PathNode19",      "CTF-Geothermal.JumpSpot17",    1,                        true)
		);
	}

        @Test
	public void test276_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Geothermal.PathNode19, end: CTF-Geothermal.JumpSpot17 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Geothermal.PathNode19",      "CTF-Geothermal.JumpSpot17",    20,                        true)
		);
	}

}
