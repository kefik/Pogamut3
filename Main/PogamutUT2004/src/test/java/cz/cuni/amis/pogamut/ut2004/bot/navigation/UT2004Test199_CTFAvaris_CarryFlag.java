package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test199_CTFAvaris_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Avaris";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test199_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Avaris.xRedFlagBase0, end: CTF-Avaris.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.xRedFlagBase0",      "CTF-Avaris.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test199_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 25 minutes
			25,
			// test movement between        start: CTF-Avaris.xRedFlagBase0, end: CTF-Avaris.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.xRedFlagBase0",      "CTF-Avaris.xBlueFlagBase1",    20,                        false)
		);
	}

}