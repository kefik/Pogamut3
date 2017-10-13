package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test216_CTFChrome_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Chrome";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test216_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Chrome.PathNode52, end: CTF-Chrome.JumpSpot42 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Chrome.PathNode52",      "CTF-Chrome.JumpSpot42",    1,                        true)
		);
	}

        @Test
	public void test216_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Chrome.PathNode52, end: CTF-Chrome.JumpSpot42 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Chrome.PathNode52",      "CTF-Chrome.JumpSpot42",    20,                        true)
		);
	}

}
