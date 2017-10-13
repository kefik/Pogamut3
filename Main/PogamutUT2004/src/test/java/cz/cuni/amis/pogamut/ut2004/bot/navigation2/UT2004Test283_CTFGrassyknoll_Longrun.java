package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test283_CTFGrassyknoll_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grassyknoll";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test283_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: CTF-Grassyknoll.PathNode394, end: CTF-Grassyknoll.PathNode383 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode394",      "CTF-Grassyknoll.PathNode383",    1,                        true)
		);
	}

        @Test
	public void test283_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 15 minutes
			15,
			// test movement between        start: CTF-Grassyknoll.PathNode394, end: CTF-Grassyknoll.PathNode383 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode394",      "CTF-Grassyknoll.PathNode383",    20,                        true)
		);
	}

}
