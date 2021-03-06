package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test215_CTFChrome_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Chrome";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test215_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Chrome.JumpSpot7, end: CTF-Chrome.JumpSpot25 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.JumpSpot7",      "CTF-Chrome.JumpSpot25",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test215_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-Chrome.JumpSpot7, end: CTF-Chrome.JumpSpot25 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Chrome.JumpSpot7",      "CTF-Chrome.JumpSpot25",    20,                        true)
		);
	}

}