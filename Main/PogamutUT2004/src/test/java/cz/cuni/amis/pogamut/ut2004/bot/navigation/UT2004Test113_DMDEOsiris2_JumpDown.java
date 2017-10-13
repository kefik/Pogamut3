package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test113_DMDEOsiris2_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Osiris2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test113_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Osiris2.PlayerStart0, end: DM-DE-Osiris2.PathNode62 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Osiris2.PlayerStart0",      "DM-DE-Osiris2.PathNode62",    1,                        false)
		);
	}

        @Test
	public void test113_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: DM-DE-Osiris2.PlayerStart0, end: DM-DE-Osiris2.PathNode62 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Osiris2.PlayerStart0",      "DM-DE-Osiris2.PathNode62",    20,                        false)
		);
	}

}