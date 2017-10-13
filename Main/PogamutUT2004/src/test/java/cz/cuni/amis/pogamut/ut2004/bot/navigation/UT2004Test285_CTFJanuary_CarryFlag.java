package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test285_CTFJanuary_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test285_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minute
			3,
			// test movement between        start: CTF-January.xRedFlagBase0, end: CTF-January.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.xRedFlagBase0",      "CTF-January.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test285_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 40 minutes
			40,
			// test movement between        start: CTF-January.xRedFlagBase0, end: CTF-January.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.xRedFlagBase0",      "CTF-January.xBlueFlagBase0",    20,                        false)
		);
	}

}