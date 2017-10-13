package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test312_CTFMaul_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Maul";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test312_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Maul.PathNode59, end: CTF-Maul.PathNode46 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode59",      "CTF-Maul.PathNode46",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test312_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Maul.PathNode59, end: CTF-Maul.PathNode46 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode59",      "CTF-Maul.PathNode46",    20,                        true)
		);
	}

}