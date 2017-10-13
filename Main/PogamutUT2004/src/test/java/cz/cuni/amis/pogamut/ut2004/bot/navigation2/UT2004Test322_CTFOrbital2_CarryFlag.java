package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test322_CTFOrbital2_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Orbital2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test322_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Orbital2.xRedFlagBase0, end: CTF-Orbital2.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Orbital2.xRedFlagBase0",      "CTF-Orbital2.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test322_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 35 minutes
			35,
			// test movement between        start: CTF-Orbital2.xRedFlagBase0, end: CTF-Orbital2.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Orbital2.xRedFlagBase0",      "CTF-Orbital2.xBlueFlagBase0",    20,                        false)
		);
	}

}
