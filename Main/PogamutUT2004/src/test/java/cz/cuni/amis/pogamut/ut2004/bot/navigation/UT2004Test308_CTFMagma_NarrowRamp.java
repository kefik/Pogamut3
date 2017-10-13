package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test308_CTFMagma_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test308_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PathNode6, end: CTF-Magma.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode6",      "CTF-Magma.PathNode12",    1,                        true)
		);
	}

        @Test
	public void test308_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 7 minutes
			7,
			// test movement between        start: CTF-Magma.PathNode6, end: CTF-Magma.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode6",      "CTF-Magma.PathNode12",    20,                        true)
		);
	}

}