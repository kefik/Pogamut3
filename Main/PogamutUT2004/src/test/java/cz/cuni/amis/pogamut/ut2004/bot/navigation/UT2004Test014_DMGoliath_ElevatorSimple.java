package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test014_DMGoliath_ElevatorSimple extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test14_elevator_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PathNode24, end: DM-Goliath.PathNode19 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PathNode24",      "DM-Goliath.PathNode19",    1,                        true)
		);
	}

        @Test
	public void test14_elevator_20_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-Goliath.PathNode24, end: DM-Goliath.PathNode19 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PathNode24",      "DM-Goliath.PathNode19",    20,                        true)
		);
	}

}
