package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test034_DMRrajigar_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rrajigar";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test34_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rrajigar.PathNode1, end: DM-Rrajigar.PathNode38 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rrajigar.PathNode1",      "DM-Rrajigar.PathNode38",    1,                        true)
		);
	}

        @Test
	public void test34_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Rrajigar.PathNode1, end: DM-Rrajigar.PathNode38 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rrajigar.PathNode1",      "DM-Rrajigar.PathNode38",    20,                        true)
		);
	}

}