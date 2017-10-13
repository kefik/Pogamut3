package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test255_CTFFace3_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test255_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.PathNode102, end: CTF-Face3.PathNode323 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.PathNode102",      "CTF-Face3.PathNode323",    1,                        true)
		);
	}

        @Test
	public void test255_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Face3.PathNode102, end: CTF-Face3.PathNode323 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.PathNode102",      "CTF-Face3.PathNode323",    20,                        false)
		);
	}

}