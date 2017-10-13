package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test224_CTFColossus_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Colossus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test224_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Colossus.PathNode76, end: CTF-Colossus.JumpSpot35 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Colossus.PathNode76",      "CTF-Colossus.JumpSpot35",    1,                        false)
		);
	}

        @Test
	public void test224_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Colossus.PathNode76, end: CTF-Colossus.JumpSpot35 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Colossus.PathNode76",      "CTF-Colossus.JumpSpot35",    20,                        false)
		);
	}

}
