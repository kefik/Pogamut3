package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test311_CTFMaul_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Maul";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test311_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Maul.AIMarker148, end: CTF-Maul.PathNode68 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Maul.AIMarker148",      "CTF-Maul.PathNode68",    1,                        false)
		);
	}

        @Test
	public void test311_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Maul.AIMarker148, end: CTF-Maul.PathNode68 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Maul.AIMarker148",      "CTF-Maul.PathNode68",    20,                        false)
		);
	}

}
