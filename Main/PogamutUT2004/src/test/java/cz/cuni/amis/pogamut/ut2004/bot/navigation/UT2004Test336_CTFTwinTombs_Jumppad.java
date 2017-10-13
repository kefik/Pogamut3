package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test336_CTFTwinTombs_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test336_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.AssaultPath19, end: CTF-TwinTombs.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.AssaultPath19",      "CTF-TwinTombs.xRedFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test336_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-TwinTombs.AssaultPath19, end: CTF-TwinTombs.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.AssaultPath19",      "CTF-TwinTombs.xRedFlagBase0",    20,                        false)
		);
	}

}