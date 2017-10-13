package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test029_DMRustatorium_Jumps extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rustatorium";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test29_jumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rustatorium.JumpSpot6, end: DM-Rustatorium.JumpSpot8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rustatorium.JumpSpot6",      "DM-Rustatorium.JumpSpot8",    1,                        false)
		);
	}

        @Test
	public void test29_jumps_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Rustatorium.JumpSpot6, end: DM-Rustatorium.JumpSpot8 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rustatorium.JumpSpot6",      "DM-Rustatorium.JumpSpot8",    20,                        false)
		);
	}

}
