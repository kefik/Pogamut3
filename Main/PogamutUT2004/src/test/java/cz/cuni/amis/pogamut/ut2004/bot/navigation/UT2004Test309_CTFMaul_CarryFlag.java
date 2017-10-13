package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test309_CTFMaul_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Maul";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test309_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Maul.xRedFlagBase0, end: CTF-Maul.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.xRedFlagBase0",      "CTF-Maul.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test309_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 40 minutes
			40,
			// test movement between        start: CTF-Maul.xRedFlagBase0, end: CTF-Maul.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.xRedFlagBase0",      "CTF-Maul.xBlueFlagBase0",    20,                        false)
		);
	}

}