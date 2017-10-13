package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test075_DMInjector_RampAndJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Injector";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test75_rampjump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Injector.InventorySpot27, end: DM-Injector.InventorySpot14 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Injector.InventorySpot27",      "DM-Injector.InventorySpot14",    1,                        true)
		);
	}

       /**
       *  TODO: Test fails
       */
        @Test
	public void test75_rampjump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Injector.InventorySpot27, end: DM-Injector.InventorySpot14 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Injector.InventorySpot27",      "DM-Injector.InventorySpot14",    20,                        true)
		);
	}

}
