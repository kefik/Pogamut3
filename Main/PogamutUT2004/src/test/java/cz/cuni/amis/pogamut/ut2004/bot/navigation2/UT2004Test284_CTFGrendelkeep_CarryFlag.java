package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test284_CTFGrendelkeep_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test284_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Grendelkeep.xRedFlagBase1, end: CTF-Grendelkeep.xBlueFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grendelkeep.xRedFlagBase1",      "CTF-Grendelkeep.xBlueFlagBase1",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test284_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 20 minutes
			25,
			// test movement between        start: CTF-Grendelkeep.xRedFlagBase1, end: CTF-Grendelkeep.xBlueFlagBase1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grendelkeep.xRedFlagBase1",      "CTF-Grendelkeep.xBlueFlagBase1",    20,                        false)
		);
	}

}
