package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test292_CTFJanuary_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test292_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.AssoultPath18, end: CTF-January.PathNode80 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.AssaultPath18",      "CTF-January.PathNode80",    1,                        false)
		);
	}

        @Test
	public void test292_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-January.AssoultPath18, end: CTF-January.PathNode80 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.AssaultPath18",      "CTF-January.PathNode80",    20,                        false)
		);
	}

}