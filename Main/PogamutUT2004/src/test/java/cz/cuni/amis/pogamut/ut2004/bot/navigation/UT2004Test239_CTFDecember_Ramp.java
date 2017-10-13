package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test239_CTFDecember_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-December";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test239_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-December.PlayerStart26, end: CTF-December.PathNode174 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-December.PlayerStart26",      "CTF-December.PathNode174",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test239_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-December.PlayerStart26, end: CTF-December.PathNode174 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-December.PlayerStart26",      "CTF-December.PathNode174",    20,                        true)
		);
	}

}