package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test253_CTFFace3_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test253_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.PathNode86, end: CTF-Face3.PathNode85 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Face3.PathNode86",      "CTF-Face3.PathNode85",    1,                        true)
		);
	}

        @Test
	public void test253_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Face3.PathNode86, end: CTF-Face3.PathNode85 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Face3.PathNode86",      "CTF-Face3.PathNode85",    20,                        true)
		);
	}

}
