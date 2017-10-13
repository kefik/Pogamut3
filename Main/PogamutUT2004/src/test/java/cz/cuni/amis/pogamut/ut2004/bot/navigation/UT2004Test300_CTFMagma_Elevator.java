package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test300_CTFMagma_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Magma";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test300_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Magma.JumpSpot31, end: CTF-Magma.JumpSpot18 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.JumpSpot31",      "CTF-Magma.JumpSpot18",    1,                        false)
		);
	}

        @Test
	public void test300_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Magma.JumpSpot31, end: CTF-Magma.JumpSpot18 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Magma.JumpSpot31",      "CTF-Magma.JumpSpot18",    20,                        false)
		);
	}

}