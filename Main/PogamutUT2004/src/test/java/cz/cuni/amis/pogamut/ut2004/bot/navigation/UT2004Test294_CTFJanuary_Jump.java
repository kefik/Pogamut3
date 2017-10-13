package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test294_CTFJanuary_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test294_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.PathNode149, end: CTF-January.PathNode209 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode149",      "CTF-January.PathNode209",    1,                        false)
		);
	}

        @Test
	public void test294_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-January.PathNode149, end: CTF-January.PathNode209 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode149",      "CTF-January.PathNode209",    20,                        false)
		);
	}

}