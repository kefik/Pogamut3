package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test201_CTFAvaris_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Avaris";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test201_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Avaris.PlayerStart13, end: CTF-Avaris.PathNode301 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Avaris.PlayerStart13",      "CTF-Avaris.PathNode301",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test201_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-Avaris.PlayerStart13, end: CTF-Avaris.PathNode301 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Avaris.PlayerStart13",      "CTF-Avaris.PathNode301",    20,                        true)
		);
	}

}
