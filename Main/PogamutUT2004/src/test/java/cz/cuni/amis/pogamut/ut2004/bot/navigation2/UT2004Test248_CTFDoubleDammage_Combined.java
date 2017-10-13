package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test248_CTFDoubleDammage_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DoubleDammage";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test248_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DoubleDammage.PathNode241, end: CTF-DoubleDammage.JumpSpot42 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DoubleDammage.PathNode241",      "CTF-DoubleDammage.JumpSpot42",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test248_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-DoubleDammage.PathNode241, end: CTF-DoubleDammage.JumpSpot42 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DoubleDammage.PathNode241",      "CTF-DoubleDammage.JumpSpot42",    20,                        true)
		);
	}

}
