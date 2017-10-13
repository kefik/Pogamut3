package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test223_CTFColossus_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Colossus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test223_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Colossus.xRedFlagBase0, end: CTF-Colossus.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.xRedFlagBase0",      "CTF-Colossus.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test223_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 25 minutes
			25,
			// test movement between        start: CTF-Colossus.xRedFlagBase0, end: CTF-Colossus.xBlueFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.xRedFlagBase0",      "CTF-Colossus.xBlueFlagBase0",    20,                        false)
		);
	}

}