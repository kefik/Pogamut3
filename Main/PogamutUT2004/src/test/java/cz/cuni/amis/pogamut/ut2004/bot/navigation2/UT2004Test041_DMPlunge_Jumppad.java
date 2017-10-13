package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test041_DMPlunge_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Plunge";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test41_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Plunge.PathNode217, end: DM-Plunge.InventorySpot740 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Plunge.PathNode217",      "DM-Plunge.InventorySpot740",    1,                        false)
		);
	}

        @Test
	public void test41_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Plunge.PathNode217, end: DM-Plunge.InventorySpot740 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Plunge.PathNode217",      "DM-Plunge.InventorySpot740",    20,                        false)
		);
	}

}
