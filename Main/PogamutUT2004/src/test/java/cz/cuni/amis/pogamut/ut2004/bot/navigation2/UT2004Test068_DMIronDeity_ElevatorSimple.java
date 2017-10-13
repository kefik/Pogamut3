package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test068_DMIronDeity_ElevatorSimple extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-IronDeity";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test68_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-IronDeity.PathNode19, end: DM-IronDeity.LiftExit3 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-IronDeity.PathNode19",      "DM-IronDeity.LiftExit3",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        * (bot probably thinks he's stuck)
        */
        @Test
	public void test68_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-IronDeity.PathNode19, end: DM-IronDeity.LiftExit3 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-IronDeity.PathNode19",      "DM-IronDeity.LiftExit3",    20,                        true)
		);
	}

}
