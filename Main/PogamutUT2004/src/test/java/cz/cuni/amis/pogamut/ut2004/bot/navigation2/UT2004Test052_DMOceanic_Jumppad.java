package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test052_DMOceanic_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Oceanic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test52_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Oceanic.PlayerStart0, end: DM-Oceanic.JumpSpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Oceanic.PlayerStart0",      "DM-Oceanic.JumpSpot1",    1,                        false)
		);
	}

        @Test
	public void test52_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Oceanic.PlayerStart0, end: DM-Oceanic.JumpSpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Oceanic.PlayerStart0",      "DM-Oceanic.JumpSpot1",    20,                        false)
		);
	}

}
