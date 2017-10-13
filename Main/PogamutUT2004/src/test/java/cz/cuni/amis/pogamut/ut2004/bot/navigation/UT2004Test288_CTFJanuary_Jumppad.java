package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test288_CTFJanuary_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test288_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.PathNode118, end: CTF-January.PathNode129 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode118",      "CTF-January.PathNode129",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test288_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-January.PathNode118, end: CTF-January.PathNode129 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode118",      "CTF-January.PathNode129",    20,                        true)
		);
	}

}