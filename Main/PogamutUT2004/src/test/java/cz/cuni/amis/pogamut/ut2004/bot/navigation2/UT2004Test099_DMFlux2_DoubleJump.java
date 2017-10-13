package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test099_DMFlux2_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Flux2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test99_doublejump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Flux2.InventorySpot62, end: DM-Flux2.JumpSpot12 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Flux2.InventorySpot62",      "DM-Flux2.JumpSpot12",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test99_doublejump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Flux2.InventorySpot62, end: DM-Flux2.JumpSpot12 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Flux2.InventorySpot62",      "DM-Flux2.JumpSpot12",    20,                        true)
		);
	}

}
