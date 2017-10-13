package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test105_DMDesertIsle_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DesertIsle";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test105_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DesertIsle.JumpSpot67, end: DM-DesertIsle.JumpSpot31 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DesertIsle.JumpSpot67",      "DM-DesertIsle.JumpSpot31",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test105_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: DM-DesertIsle.JumpSpot67, end: DM-DesertIsle.JumpSpot31 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DesertIsle.JumpSpot67",      "DM-DesertIsle.JumpSpot31",    20,                        false)
		);
	}

}