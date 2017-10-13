package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test056_DMMorpheus3_Jumpdown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Morpheus3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test56_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Morpheus3.PathNode105, end: DM-Morpheus3.JumpSpot0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode105",      "DM-Morpheus3.JumpSpot0",    1,                        false)
		);
	}

        @Test
	public void test56_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-Morpheus3.PathNode105, end: DM-Morpheus3.JumpSpot0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode105",      "DM-Morpheus3.JumpSpot0",    20,                        false)
		);
	}

}
