package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test279_CTFGrassyknoll_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grassyknoll";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test279_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minute
			3,
			// test movement between        start: CTF-Grassyknoll.xRedFlagBase1, end: CTF-Grassyknoll.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Grassyknoll.xRedFlagBase1",      "CTF-Grassyknoll.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test279_flag_20_time() {



		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 30 minutes
			30,
			// test movement between        start: CTF-Grassyknoll.xRedFlagBase1, end: CTF-Grassyknoll.xBlueFlagBase1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Grassyknoll.xRedFlagBase1",      "CTF-Grassyknoll.xBlueFlagBase1",    20,                        false)
		);
	}

}