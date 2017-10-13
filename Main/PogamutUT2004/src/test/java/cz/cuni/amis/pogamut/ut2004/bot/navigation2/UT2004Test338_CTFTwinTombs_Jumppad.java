package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test338_CTFTwinTombs_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test338_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.PathNode1, end: CTF-TwinTombs.AssaultPath9 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.PathNode1",      "CTF-TwinTombs.AssaultPath9",    1,                        false)
		);
	}

        @Test
	public void test338_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-TwinTombs.PathNode1, end: CTF-TwinTombs.AssaultPath9 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.PathNode1",      "CTF-TwinTombs.AssaultPath9",    20,                        false)
		);
	}

}
