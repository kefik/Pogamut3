package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test268_CTFFace3_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test268_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.PathNode394, end: CTF-Face3.PathNode509 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Face3.PathNode394",      "CTF-Face3.PathNode509",    1,                        false)
		);
	}

        @Test
	public void test268_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-Face3.PathNode394, end: CTF-Face3.PathNode509 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Face3.PathNode394",      "CTF-Face3.PathNode509",    20,                        false)
		);
	}

}
