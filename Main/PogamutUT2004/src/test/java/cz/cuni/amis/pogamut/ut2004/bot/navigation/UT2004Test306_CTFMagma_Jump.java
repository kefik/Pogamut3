package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test306_CTFMagma_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test306_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PathNode111, end: CTF-Magma.JumpSpot67 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode111",      "CTF-Magma.JumpSpot67",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test306_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Magma.PathNode111, end: CTF-Magma.JumpSpot67 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode111",      "CTF-Magma.JumpSpot67",    20,                        true)
		);
	}

}