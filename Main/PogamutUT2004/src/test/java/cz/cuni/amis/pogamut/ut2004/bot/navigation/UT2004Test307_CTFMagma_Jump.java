package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test307_CTFMagma_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test307_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.PathNode60, end: CTF-Magma.JumpSpot34 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode60",      "CTF-Magma.JumpSpot34",    1,                        true)
		);
	}

        @Test
	public void test307_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Magma.PathNode60, end: CTF-Magma.JumpSpot34 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.PathNode60",      "CTF-Magma.JumpSpot34",    20,                        true)
		); 
	}

}