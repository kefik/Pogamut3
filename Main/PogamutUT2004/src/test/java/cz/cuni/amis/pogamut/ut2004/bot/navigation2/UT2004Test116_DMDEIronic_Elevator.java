package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test116_DMDEIronic_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Ironic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test116_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Ironic.PlayerStart6, end: DM-DE-Ironic.PlayerStart7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Ironic.PlayerStart6",      "DM-DE-Ironic.PlayerStart7",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test116_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Ironic.PlayerStart6, end: DM-DE-Ironic.PlayerStart7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DE-Ironic.PlayerStart6",      "DM-DE-Ironic.PlayerStart7",    20,                        true)
		);
	}

}
