package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test302_CTFMagma_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test302_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PlayerStart2, end: CTF-Magma.PathNode123 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PlayerStart2",      "CTF-Magma.PathNode123",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test302_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Magma.PlayerStart2, end: CTF-Magma.PathNode123 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PlayerStart2",      "CTF-Magma.PathNode123",    20,                        true)
		);
	}

}