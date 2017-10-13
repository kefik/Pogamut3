package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test314_CTFMoonDragon_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test314_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minute
			3,
			// test movement between        start: CTF-MoonDragon.xRedFlagBase0, end: CTF-MoonDragon.xBlueFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-MoonDragon.xRedFlagBase0",      "CTF-MoonDragon.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test314_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 50 minutes
			50,
			// test movement between        start: CTF-MoonDragon.xRedFlagBase0, end: CTF-MoonDragon.xBlueFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-MoonDragon.xRedFlagBase0",      "CTF-MoonDragon.xBlueFlagBase1",    20,                        false)
		);
	}

}
