package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test287_CTFJanuary_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test287_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.PathNode117, end: CTF-January.PathNode180 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode117",      "CTF-January.PathNode180",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test287_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-January.PathNode117, end: CTF-January.PathNode180 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode117",      "CTF-January.PathNode180",    20,                        false)
		);
	}

}