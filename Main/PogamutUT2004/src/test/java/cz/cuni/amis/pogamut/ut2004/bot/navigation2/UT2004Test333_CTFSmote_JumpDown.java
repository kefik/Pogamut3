package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test333_CTFSmote_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Smote";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test333_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Smote.JumpSpot22, end: CTF-Smote.JumpSpot25 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.JumpSpot22",      "CTF-Smote.JumpSpot25",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test333_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Smote.JumpSpot22, end: CTF-Smote.JumpSpot25 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Smote.JumpSpot22",      "CTF-Smote.JumpSpot25",    20,                        false)
		);
	}

}
