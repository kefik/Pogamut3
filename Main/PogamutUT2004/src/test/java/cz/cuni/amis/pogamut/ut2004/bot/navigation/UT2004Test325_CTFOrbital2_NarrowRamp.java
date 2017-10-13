package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test325_CTFOrbital2_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Orbital2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test235_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Orbital2.PathNode240, end: CTF-Orbital2.PathNode245 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Orbital2.PathNode240",      "CTF-Orbital2.PathNode245",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test235_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Orbital2.PathNode240, end: CTF-Orbital2.PathNode245 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Orbital2.PathNode240",      "CTF-Orbital2.PathNode245",    20,                        true)
		);
	}

}