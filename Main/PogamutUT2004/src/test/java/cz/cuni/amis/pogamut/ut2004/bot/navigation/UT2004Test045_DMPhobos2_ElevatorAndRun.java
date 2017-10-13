package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test045_DMPhobos2_ElevatorAndRun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Phobos2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test43_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Phobos.PathNode67, end: DM-Phobos.AIMarker17 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode67",      "DM-Phobos2.AIMarker17",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test43_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Phobos.PathNode67, end: DM-Phobos.AIMarker17 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode67",      "DM-Phobos2.AIMarker17",    20,                        true)
		);
	}

}