package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test240_CTFDecember_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-December";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test240_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-December.JumpSpot28, end: CTF-December.JumpSpot27 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.JumpSpot28",      "CTF-December.JumpSpot27",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test240_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-December.JumpSpot28, end: CTF-December.JumpSpot27 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.JumpSpot28",      "CTF-December.JumpSpot27",    20,                        true)
		);
	}

}
