package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test238_CTFDecember_Door extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-December";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test238_door_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-December.PathNode152, end: CTF-December.PlayerStart20 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.PathNode152",      "CTF-December.PlayerStart20",    1,                        true)
		);
	}

        @Test
	public void test238_door_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-December.PathNode152, end: CTF-December.PlayerStart20 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.PathNode152",      "CTF-December.PlayerStart20",    20,                        true)
		);
	}

}
