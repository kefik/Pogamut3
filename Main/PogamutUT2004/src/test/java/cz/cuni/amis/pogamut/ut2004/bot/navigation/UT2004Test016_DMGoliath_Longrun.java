package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test016_DMGoliath_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test16_longrun_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PlayerStart2, end: DM-Goliath.InventorySpot148 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PlayerStart2",      "DM-Goliath.InventorySpot148",    1,                        true)
		);
	}

        @Test
	public void test16_elongrun_20_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 11 minutes
			11,
			// test movement between        start: DM-Goliath.PlayerStart2, end: DM-Goliath.InventorySpot148 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PlayerStart2",      "DM-Goliath.InventorySpot148",    20,                        true)
		);
	}

}
