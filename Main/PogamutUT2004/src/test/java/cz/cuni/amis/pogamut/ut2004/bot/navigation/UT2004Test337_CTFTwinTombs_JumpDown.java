package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test337_CTFTwinTombs_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test337_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.PlayerStart55, end: CTF-TwinTombs.PathNode214 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.PlayerStart55",      "CTF-TwinTombs.PathNode214",    1,                        false)
		);
	}

        @Test
	public void test337_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-TwinTombs.PlayerStart55, end: CTF-TwinTombs.PathNode214 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.PlayerStart55",      "CTF-TwinTombs.PathNode214",    20,                        false)
		);
	}

}