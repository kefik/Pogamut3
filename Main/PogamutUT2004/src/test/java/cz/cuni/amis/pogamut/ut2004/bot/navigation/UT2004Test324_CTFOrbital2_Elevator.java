package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test324_CTFOrbital2_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Orbital2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test324_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Orbital2.LiftExit18, end: CTF-Orbital2.LiftExit19 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Orbital2.LiftExit18",      "CTF-Orbital2.LiftExit19",    1,                        true)
		);
	}

        @Test
	public void test324_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Orbital2.LiftExit18, end: CTF-Orbital2.LiftExit19 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Orbital2.LiftExit18",      "CTF-Orbital2.LiftExit19",    20,                        true)
		);
	}

}