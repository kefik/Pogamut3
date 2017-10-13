package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test141_DMAsbestos_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Asbestos";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test141_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Asbestos.PathNode183, end: DM-Asbestos.InventorySpot18 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Asbestos.PathNode183",      "DM-Asbestos.InventorySpot18",    1,                        true)
		);
	}

        @Test
	public void test141_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 7 minutes
			7,
			// test movement between        start: DM-Asbestos.PathNode183, end: DM-Asbestos.InventorySpot18 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Asbestos.PathNode183",      "DM-Asbestos.InventorySpot18",    20,                        true)
		);
	}

}
