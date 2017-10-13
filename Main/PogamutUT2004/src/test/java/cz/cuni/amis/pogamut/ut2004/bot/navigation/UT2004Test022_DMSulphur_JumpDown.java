package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test022_DMSulphur_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Sulphur";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test22_jumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Sulphur.PathNode38, end: DM-Sulphur.PlayerStart4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Sulphur.PathNode38",      "DM-Sulphur.PlayerStart4",    1,                        false)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test22_jumps_20_time() {

           
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 7 minutes
			7,
			// test movement between        start: DM-Sulphur.PathNode38, end: DM-Sulphur.PlayerStart4 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Sulphur.PathNode38",      "DM-Sulphur.PlayerStart4",    20,                        false)
		);
	}

}
