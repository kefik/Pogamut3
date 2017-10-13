package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test317_CTFMoonDragon_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test317_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode515, end: CTF-MoonDragon.PathNode512 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode515",      "CTF-MoonDragon.PathNode512",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test317_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-MoonDragon.PathNode515, end: CTF-MoonDragon.PathNode512 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode515",      "CTF-MoonDragon.PathNode512",    20,                        false)
		);
	}

}