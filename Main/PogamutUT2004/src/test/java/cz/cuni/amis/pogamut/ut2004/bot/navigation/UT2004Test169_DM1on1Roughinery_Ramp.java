package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test169_DM1on1Roughinery_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Roughinery";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test169_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Roughinery.PathNode80, end: DM-1on1-Roughinery.PathNode10 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Roughinery.PathNode80",      "DM-1on1-Roughinery.PathNode10",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test169_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Roughinery.PathNode80, end: DM-1on1-Roughinery.PathNode10 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Roughinery.PathNode80",      "DM-1on1-Roughinery.PathNode10",    20,                        true)
		);
	}

}