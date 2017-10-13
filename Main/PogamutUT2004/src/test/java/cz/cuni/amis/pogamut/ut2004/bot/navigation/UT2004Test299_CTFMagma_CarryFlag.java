package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test299_CTFMagma_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test299_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minute
			3,
			// test movement between        start: CTF-Magma.xRedFlagBase1, end: CTF-Magma.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.xRedFlagBase1",      "CTF-Magma.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test299_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 50 minutes
			50,
			// test movement between        start: CTF-Magma.xRedFlagBase1, end: CTF-Magma.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.xRedFlagBase1",      "CTF-Magma.xBlueFlagBase0",    20,                        false)
		);
	}

}