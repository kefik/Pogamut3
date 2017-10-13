package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test298_CTFLostFaith_NarrowCorridor extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Lostfaith";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test298_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Lostfaith.AssaultPath18, end: CTF-Lostfaith.PathNode136 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.AssaultPath18",      "CTF-Lostfaith.PathNode136",    1,                        true)
		);
	}

        @Test
	public void test298_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-Lostfaith.AssaultPath18, end: CTF-Lostfaith.PathNode136 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Lostfaith.AssaultPath18",      "CTF-Lostfaith.PathNode136",    20,                        true)
		);
	}

}