package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test127_DMCorrugation_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Corrugation";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test127_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Corrugation.PlayerStart17, end: DM-Corrugation.LiftExit3 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Corrugation.PlayerStart17",      "DM-Corrugation.LiftExit3",    1,                        false)
		);
	}

        @Test
	public void test127_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Corrugation.PlayerStart17, end: DM-Corrugation.LiftExit3 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Corrugation.PlayerStart17",      "DM-Corrugation.LiftExit3",    20,                        false)
		);
	}

}