package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test195_CTFAbsoluteZero_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-AbsoluteZero";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test195_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-AbsoluteZero.xRedFlagBase1, end: CTF-AbsoluteZero.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-AbsoluteZero.xRedFlagBase1",      "CTF-AbsoluteZero.xBlueFlagBase0",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test195_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 20 minutes
			20,
			// test movement between        start: CTF-AbsoluteZero.xRedFlagBase1, end: CTF-AbsoluteZero.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-AbsoluteZero.xRedFlagBase1",      "CTF-AbsoluteZero.xBlueFlagBase0",    20,                        true)
		);
	}

}