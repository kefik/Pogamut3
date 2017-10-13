package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test092_DMGestalt_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test92_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.JumpSpot25, end: DM-Gestalt.JumpSpot21 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.JumpSpot25",      "DM-Gestalt.JumpSpot21",    1,                        false)
		);
	}

        @Test
	public void test92_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Gestalt.JumpSpot25, end: DM-Gestalt.JumpSpot21 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.JumpSpot25",      "DM-Gestalt.JumpSpot21",    20,                        false)
		);
	}

}