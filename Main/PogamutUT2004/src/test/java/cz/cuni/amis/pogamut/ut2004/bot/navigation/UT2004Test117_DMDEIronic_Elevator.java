package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test117_DMDEIronic_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Ironic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test117_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Ironic.PlayerStart7, end: DM-DE-Ironic.PlayerStart15 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.PlayerStart7",      "DM-DE-Ironic.PlayerStart15",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test117_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-DE-Ironic.PlayerStart7, end: DM-DE-Ironic.PlayerStart15 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.PlayerStart7",      "DM-DE-Ironic.PlayerStart15",    20,                        false)
		);
	}

}