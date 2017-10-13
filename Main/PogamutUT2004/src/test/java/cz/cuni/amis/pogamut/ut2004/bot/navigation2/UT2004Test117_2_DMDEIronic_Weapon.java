package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test117_2_DMDEIronic_Weapon extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Ironic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

    @Test
	public void test117_2_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Ironic.PlayerStart7, end: DM-DE-Ironic.InventorySpot108 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Ironic.PlayerStart7",      "DM-DE-Ironic.InventorySpot108",    1,                        false)
		);
	}

    @Test
	public void test117_2_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-DE-Ironic.PlayerStart7, end: DM-DE-Ironic.InventorySpot108 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Ironic.PlayerStart7",      "DM-DE-Ironic.InventorySpot108",    20,                        false)
		);
	}

}
