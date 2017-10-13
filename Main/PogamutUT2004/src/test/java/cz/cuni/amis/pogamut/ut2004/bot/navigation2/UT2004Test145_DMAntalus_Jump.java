package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test145_DMAntalus_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Antalus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test145_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Antalus.AIMarker9, end: DM-Antalus.InventorySpot127 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.AIMarker9",      "DM-Antalus.InventorySpot127",    1,                        false)
		);
	}

        @Test
	public void test145_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Antalus.AIMarker9, end: DM-Antalus.InventorySpot127 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.AIMarker9",      "DM-Antalus.InventorySpot127",    20,                        false)
		);
	}

}
