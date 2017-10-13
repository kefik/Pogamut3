package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test249_CTFDoubleDammage_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DoubleDammage";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test249_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DoubleDammage.PathNode270, end: CTF-DoubleDammage.PathNode339 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode270",      "CTF-DoubleDammage.PathNode339",    1,                        false)
		);
	}

        @Test
	public void test249_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-DoubleDammage.PathNode270, end: CTF-DoubleDammage.PathNode339 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode270",      "CTF-DoubleDammage.PathNode339",    20,                        false)
		);
	}

}