package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test217_CTFCitadel_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test217_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.xRedFlagBase0, end: CTF-Citadel.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.xRedFlagBase0",      "CTF-Citadel.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test217_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 20 minutes
			20,
			// test movement between        start: CTF-Citadel.xRedFlagBase0, end: CTF-Citadel.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Citadel.xRedFlagBase0",      "CTF-Citadel.xBlueFlagBase0",    20,                        false)
		);
	}

}