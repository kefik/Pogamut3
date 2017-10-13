package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test159_DM1on1Spirit_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Spirit";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test159_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Spirit.PathNode62, end: DM-1on1-Spirit.PathNode51 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Spirit.PathNode62",      "DM-1on1-Spirit.PathNode51",    1,                        false)
		);
	}

        @Test
	public void test159_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Spirit.PathNode62, end: DM-1on1-Spirit.PathNode51 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Spirit.PathNode62",      "DM-1on1-Spirit.PathNode51",    20,                        false)
		);
	}

}