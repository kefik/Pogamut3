package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test334_CTFSmote_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Smote";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test334_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Smote.JumpSpot1, end: CTF-Smote.PlayerStart46 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Smote.JumpSpot1",      "CTF-Smote.PlayerStart46",    1,                        true)
		);
	}

        @Test
	public void test334_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-Smote.JumpSpot1, end: CTF-Smote.PlayerStart46 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Smote.JumpSpot1",      "CTF-Smote.PlayerStart46",    20,                        true)
		);
	}

}