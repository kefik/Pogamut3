package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test197_CTFAbsoluteZero_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-AbsoluteZero";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test197_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-AbsoluteZero.AIMarker28, end: CTF-AbsoluteZero.PathNode278 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-AbsoluteZero.AIMarker28",      "CTF-AbsoluteZero.PathNode278",    1,                        true)
		);
	}

        @Test
	public void test197_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-AbsoluteZero.AIMarker28, end: CTF-AbsoluteZero.PathNode278 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-AbsoluteZero.AIMarker28",      "CTF-AbsoluteZero.PathNode278",    20,                        true)
		);
	}

}
