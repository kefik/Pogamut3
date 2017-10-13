package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test296_CTFLostfaith_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Lostfaith";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void testnarrow_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Lostfaith.PathNode25, end: CTF-Lostfaith.PathNode108 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.PathNode25",      "CTF-Lostfaith.PathNode108",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void testnarrow_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Lostfaith.PathNode25, end: CTF-Lostfaith.PathNode108 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.PathNode25",      "CTF-Lostfaith.PathNode108",    20,                        true)
		);
	}

}