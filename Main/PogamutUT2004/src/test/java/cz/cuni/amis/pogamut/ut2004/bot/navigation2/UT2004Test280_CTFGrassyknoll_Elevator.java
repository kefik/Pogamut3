package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test280_CTFGrassyknoll_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grassyknoll";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test280_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Grassyknoll.PathNode402, end: CTF-Grassyknoll.PathNode99 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode402",      "CTF-Grassyknoll.PathNode99",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test280_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Grassyknoll.PathNode402, end: CTF-Grassyknoll.PathNode99 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Grassyknoll.PathNode402",      "CTF-Grassyknoll.PathNode99",    20,                        true)
		);
	}

}
