package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test219_CTFCitadel_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test219_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.PlayerStart9, end: CTF-Citadel.PathNode75 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Citadel.PlayerStart9",      "CTF-Citadel.PathNode75",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test219_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 12 minutes
			12,
			// test movement between        start: CTF-Citadel.PlayerStart9, end: CTF-Citadel.PathNode75 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Citadel.PlayerStart9",      "CTF-Citadel.PathNode75",    20,                        true)
		);
	}

}
