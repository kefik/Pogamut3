package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test118_DMDEGrendelkeep_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test118_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Grendelkeep.PathNode65, end: DM-DE-Grendelkeep.PathNode45 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.PathNode65",      "DM-DE-Grendelkeep.PathNode45",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test118_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Grendelkeep.PathNode65, end: DM-DE-Grendelkeep.PathNode45 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.PathNode65",      "DM-DE-Grendelkeep.PathNode45",    20,                        true)
		);
	}

}