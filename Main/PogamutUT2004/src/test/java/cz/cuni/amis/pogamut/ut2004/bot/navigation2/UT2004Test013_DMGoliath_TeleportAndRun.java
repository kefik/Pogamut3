package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test013_DMGoliath_TeleportAndRun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

    @Test
	public void test13_teleport_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PlayerStart3, end: DM-Goliath.InventorySpot191 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Goliath.PlayerStart3",      "DM-Goliath.InventorySpot191",    1,                        true)
		);
	}

    @Test
	public void test13_teleport_20_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-Goliath.PlayerStart3, end: DM-Goliath.InventorySpot191 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Goliath.PlayerStart3",      "DM-Goliath.InventorySpot191",    20,                        true)
		);
	}

}
