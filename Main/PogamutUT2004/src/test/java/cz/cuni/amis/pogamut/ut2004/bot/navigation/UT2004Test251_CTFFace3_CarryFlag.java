package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test251_CTFFace3_CarryFlag  extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test251_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.xRedFlagBase0, end: CTF-Face3.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.xRedFlagBase0",      "CTF-Face3.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test251_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 30 minutes
			30,
			// test movement between        start: CTF-Face3.xRedFlagBase0, end: CTF-Face3.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.xRedFlagBase0",      "CTF-Face3.xBlueFlagBase0",    20,                        false)
		);
	}

}