package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test282_CTFGrassyknoll_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grassyknoll";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test282_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Grassyknoll.PathNode119, end: CTF-Grassyknoll.PathNode109 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode119",      "CTF-Grassyknoll.PathNode109",    1,                        true)
		);
	}

        @Test
	public void test282_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Grassyknoll.PathNode119, end: CTF-Grassyknoll.PathNode109 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode119",      "CTF-Grassyknoll.PathNode109",    20,                        true)
		);
	}

}
