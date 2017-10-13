package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test162_DM1on1Serpentine_NarrowCorridor extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Serpentine";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test162_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Serpentine.PathNode49, end: DM-1on1-Serpentine.PathNode59 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Serpentine.PathNode49",      "DM-1on1-Serpentine.PathNode59",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test162_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Serpentine.PathNode49, end: DM-1on1-Serpentine.PathNode59 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Serpentine.PathNode49",      "DM-1on1-Serpentine.PathNode59",    20,                        true)
		);
	}

}