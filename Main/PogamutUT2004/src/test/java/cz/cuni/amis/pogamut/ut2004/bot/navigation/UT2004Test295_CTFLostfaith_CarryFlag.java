package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test295_CTFLostfaith_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Lostfaith";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test295_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Lostfaith.xRedFlagBase1, end: CTF-Lostfaith.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.xRedFlagBase1",      "CTF-Lostfaith.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test295_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 35 minutes
			35,
			// test movement between        start: CTF-Lostfaith.xRedFlagBase1, end: CTF-Lostfaith.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.xRedFlagBase1",      "CTF-Lostfaith.xBlueFlagBase1",    20,                        false)
		);
	}

}