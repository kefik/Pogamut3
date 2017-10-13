package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test254_CTFFace3_Door extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Face3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test254_door_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Face3.LiftExit17, end: CTF-Face3.JumpSpot105 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.LiftExit17",      "CTF-Face3.JumpSpot105",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test254_door_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-Face3.LiftExit17, end: CTF-Face3.JumpSpot105 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Face3.LiftExit17",      "CTF-Face3.JumpSpot105",    20,                        true)
		);
	}

}