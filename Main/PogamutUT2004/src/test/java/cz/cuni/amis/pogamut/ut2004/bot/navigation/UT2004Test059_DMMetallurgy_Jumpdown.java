package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test059_DMMetallurgy_Jumpdown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Metallurgy";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test59_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Metallurgy.PathNode57, end: DM-Metallurgy.InventorySpot68 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Metallurgy.PathNode57",      "DM-Metallurgy.InventorySpot68",    1,                        false)
		);
	}


        /**
        * TODO: Test fails
        */
        @Test
	public void test59_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Metallurgy.PathNode57, end: DM-Metallurgy.InventorySpot68 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Metallurgy.PathNode57",      "DM-Metallurgy.InventorySpot68",    20,                        false)
		);
	}

}