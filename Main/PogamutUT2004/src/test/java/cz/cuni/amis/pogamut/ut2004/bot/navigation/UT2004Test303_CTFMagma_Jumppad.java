package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test303_CTFMagma_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test303_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PathNode116, end: CTF-Magma.PathNode139 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode116",      "CTF-Magma.PathNode139",    1,                        false)
		);
	}

        @Test
	public void test303_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Magma.PathNode116, end: CTF-Magma.PathNode139 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode116",      "CTF-Magma.PathNode139",    20,                        false)
		);
	}

}