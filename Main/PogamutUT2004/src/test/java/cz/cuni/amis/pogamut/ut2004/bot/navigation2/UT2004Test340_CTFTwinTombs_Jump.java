package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test340_CTFTwinTombs_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test340_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.PathNode129, end: CTF-TwinTombs.PathNode116 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.PathNode129",      "CTF-TwinTombs.PathNode116",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test340_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-TwinTombs.PathNode129, end: CTF-TwinTombs.PathNode116 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-TwinTombs.PathNode129",      "CTF-TwinTombs.PathNode116",    20,                        true)
		);
	}

}
