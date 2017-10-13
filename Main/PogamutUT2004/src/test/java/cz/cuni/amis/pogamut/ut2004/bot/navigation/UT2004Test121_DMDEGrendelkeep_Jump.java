package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test121_DMDEGrendelkeep_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test121_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Grendelkeep.InventorySpot541, end: DM-DE-Grendelkeep.PathNode32 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.InventorySpot541",      "DM-DE-Grendelkeep.PathNode32",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test121_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-DE-Grendelkeep.InventorySpot541, end: DM-DE-Grendelkeep.PathNode32 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.InventorySpot541",      "DM-DE-Grendelkeep.PathNode32",    20,                        false)
		);
	}

}