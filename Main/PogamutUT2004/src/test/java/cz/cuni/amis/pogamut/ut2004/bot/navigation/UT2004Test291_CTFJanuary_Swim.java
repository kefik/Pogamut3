package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test291_CTFJanuary_Swim extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-January";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test291_swim_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-January.PathNode6, end: CTF-January.AssaultPath14 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode6",      "CTF-January.AssaultPath14",    1,                        false)
		);
	}

        @Test
	public void test291_swim_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-January.PathNode6, end: CTF-January.AssaultPath14 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-January.PathNode6",      "CTF-January.AssaultPath14",    20,                        false)
		);
	}

}