package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test227_CTFColossus_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Colossus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test227_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Colossus.PlayerStart0, end: CTF-Colossus.PathNode576 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Colossus.PlayerStart0",      "CTF-Colossus.PathNode576",    1,                        false)
		);
	}

        @Test
	public void test227_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Colossus.PlayerStart0, end: CTF-Colossus.PathNode576 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Colossus.PlayerStart0",      "CTF-Colossus.PathNode576",    20,                        false)
		);
	}

}
