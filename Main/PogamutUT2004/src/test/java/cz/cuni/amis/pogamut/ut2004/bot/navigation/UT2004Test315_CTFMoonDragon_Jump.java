package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test315_CTFMoonDragon_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test315_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode308, end: CTF-MoonDragon.JumpSpot132 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode308",      "CTF-MoonDragon.JumpSpot132",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test315_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-MoonDragon.PathNode308, end: CTF-MoonDragon.JumpSpot132 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode308",      "CTF-MoonDragon.JumpSpot132",    20,                        false)
		);
	}

}