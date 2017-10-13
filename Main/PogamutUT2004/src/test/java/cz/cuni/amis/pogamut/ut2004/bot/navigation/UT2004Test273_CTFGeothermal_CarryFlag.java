package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test273_CTFGeothermal_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Geothermal";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test273_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Geothermal.xRedFlagBase1, end: CTF-Geothermal.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.xRedFlagBase1",      "CTF-Geothermal.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test273_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 25 minutes
			25,
			// test movement between        start: CTF-Geothermal.xRedFlagBase1, end: CTF-Geothermal.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.xRedFlagBase1",      "CTF-Geothermal.xBlueFlagBase0",    20,                        false)
		);
	}

}