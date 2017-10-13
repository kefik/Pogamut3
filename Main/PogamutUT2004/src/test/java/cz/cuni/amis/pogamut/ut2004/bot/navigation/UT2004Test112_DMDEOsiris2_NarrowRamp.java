package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test112_DMDEOsiris2_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Osiris2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test112_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Osiris2.PathNode59, end: DM-DE-Osiris2.InventorySpot58 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Osiris2.PathNode59",      "DM-DE-Osiris2.InventorySpot58",    1,                        true)
		);
	}

        @Test
	public void test112_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Osiris2.PathNode59, end: DM-DE-Osiris2.InventorySpot58 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Osiris2.PathNode59",      "DM-DE-Osiris2.InventorySpot58",    20,                        true)
		);
	}

}