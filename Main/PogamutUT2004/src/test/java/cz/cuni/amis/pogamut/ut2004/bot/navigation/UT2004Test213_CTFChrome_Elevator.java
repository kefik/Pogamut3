package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test213_CTFChrome_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Chrome";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test213_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Chrome.LiftExit5, end: CTF-Chrome.JumpSpot24 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.LiftExit5",      "CTF-Chrome.JumpSpot24",    1,                        true)
		);
	}

        @Test
	public void test213_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Chrome.LiftExit5, end: CTF-Chrome.JumpSpot24 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.LiftExit5",      "CTF-Chrome.JumpSpot24",    20,                        true)
		);
	}

}