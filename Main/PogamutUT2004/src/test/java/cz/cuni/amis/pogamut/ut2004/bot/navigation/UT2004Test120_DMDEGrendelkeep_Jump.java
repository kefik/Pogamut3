package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test120_DMDEGrendelkeep_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test120_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Grendelkeep.PathNode36, end: DM-DE-Grendelkeep.JumpSpot5 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.PathNode36",      "DM-DE-Grendelkeep.JumpSpot5",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test120_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Grendelkeep.PathNode36, end: DM-DE-Grendelkeep.JumpSpot5 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.PathNode36",      "DM-DE-Grendelkeep.JumpSpot5",    20,                        false)
		);
	}

}