package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test252_CTFFace3_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test252_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.PathNode557, end: CTF-Face3.LiftExit16 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.PathNode557",      "CTF-Face3.LiftExit16",    1,                        false)
		);
	}

        @Test
	public void test252_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-Face3.PathNode557, end: CTF-Face3.LiftExit16 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.PathNode557",      "CTF-Face3.LiftExit16",    20,                        false)
		);
	}

}