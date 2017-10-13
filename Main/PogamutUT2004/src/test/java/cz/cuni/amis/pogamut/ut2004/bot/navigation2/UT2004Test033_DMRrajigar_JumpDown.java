package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test033_DMRrajigar_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rrajigar";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test33_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rrajigar.PathNode166, end: DM-Rrajigar.PathNode125 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rrajigar.PathNode166",      "DM-Rrajigar.PathNode125",    1,                        false)
		);
	}

        @Test
	public void test33_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Rrajigar.PathNode166, end: DM-Rrajigar.PathNode125 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rrajigar.PathNode166",      "DM-Rrajigar.PathNode125",    20,                        false)
		);
	}

}
