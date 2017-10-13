package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test329_CTFSmote_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Smote";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test329_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minute
			3,
			// test movement between        start: CTF-Smote.xRedFlagBase2, end: CTF-Smote.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.xRedFlagBase2",      "CTF-Smote.xBlueFlagBase0",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test329_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 55 minutes
			55,
			// test movement between        start: CTF-Smote.xRedFlagBase2, end: CTF-Smote.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.xRedFlagBase2",      "CTF-Smote.xBlueFlagBase0",    20,                        false)
		);
	}

}
