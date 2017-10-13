package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test119_DMDEGrendelkeep_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test119_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Grendelkeep.JumpSpot26, end: DM-DE-Grendelkeep.InventorySpot563 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.JumpSpot26",      "DM-DE-Grendelkeep.InventorySpot563",    1,                        true)
		);
	}

        @Test
	public void test119_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Grendelkeep.JumpSpot26, end: DM-DE-Grendelkeep.InventorySpot563 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Grendelkeep.JumpSpot26",      "DM-DE-Grendelkeep.InventorySpot563",    20,                        true)
		);
	}

}