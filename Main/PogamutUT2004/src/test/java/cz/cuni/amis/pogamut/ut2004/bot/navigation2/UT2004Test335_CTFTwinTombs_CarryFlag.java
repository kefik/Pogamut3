package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test335_CTFTwinTombs_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test335_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-TwinTombs.xRedFlagBase0, end: CTF-TwinTombs.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.xRedFlagBase0",      "CTF-TwinTombs.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test335_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 40 minutes
			40,
			// test movement between        start: CTF-TwinTombs.xRedFlagBase0, end: CTF-TwinTombs.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.xRedFlagBase0",      "CTF-TwinTombs.xBlueFlagBase0",    20,                        false)
		);
	}

}
