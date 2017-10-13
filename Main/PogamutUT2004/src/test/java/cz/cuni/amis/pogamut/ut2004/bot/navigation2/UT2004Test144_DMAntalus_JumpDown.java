package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test144_DMAntalus_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Antalus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test144_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Antalus.JumpSpot32, end: DM-Antalus.JumpSpot9 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.JumpSpot32",      "DM-Antalus.JumpSpot9",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test144_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Antalus.JumpSpot32, end: DM-Antalus.JumpSpot9 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.JumpSpot32",      "DM-Antalus.JumpSpot9",    20,                        false)
		);
	}

}
