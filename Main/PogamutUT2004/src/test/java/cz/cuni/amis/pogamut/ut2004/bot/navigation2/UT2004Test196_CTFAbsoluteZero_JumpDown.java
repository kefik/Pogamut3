package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test196_CTFAbsoluteZero_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-AbsoluteZero";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test196_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-AbsoluteZero.PathNode95, end: CTF-AbsoluteZero.JumpSpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-AbsoluteZero.PathNode95",      "CTF-AbsoluteZero.JumpSpot1",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test196_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-AbsoluteZero.PathNode95, end: CTF-AbsoluteZero.JumpSpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-AbsoluteZero.PathNode95",      "CTF-AbsoluteZero.JumpSpot1",    20,                        false)
		);
	}

}
