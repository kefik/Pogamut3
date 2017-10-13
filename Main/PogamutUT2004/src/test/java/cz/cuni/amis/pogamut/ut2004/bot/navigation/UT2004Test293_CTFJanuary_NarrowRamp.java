package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test293_CTFJanuary_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test293_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.PathNode144, end: CTF-January.PathNode138 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode144",      "CTF-January.PathNode138",    1,                        true)
		);
	}

        @Test
	public void test293_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-January.PathNode144, end: CTF-January.PathNode138 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode144",      "CTF-January.PathNode138",    20,                        true)
		);
	}

}