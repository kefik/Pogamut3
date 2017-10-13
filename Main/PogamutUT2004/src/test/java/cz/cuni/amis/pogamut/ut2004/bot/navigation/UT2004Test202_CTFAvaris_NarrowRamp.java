package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test202_CTFAvaris_NarrowRamp  extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Avaris";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test202_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Avaris.PathNode64, end: CTF-Avaris.PathNode7 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.PathNode64",      "CTF-Avaris.PathNode7",    1,                        true)
		);
	}

        @Test
	public void test202_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Avaris.PathNode64, end: CTF-Avaris.PathNode7 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.PathNode64",      "CTF-Avaris.PathNode7",    20,                        true)
		);
	}

}