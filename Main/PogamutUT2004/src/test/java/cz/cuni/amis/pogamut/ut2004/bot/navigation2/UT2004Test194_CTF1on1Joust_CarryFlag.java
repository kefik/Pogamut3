package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test194_CTF1on1Joust_CarryFlag extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-1on1-Joust";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test194_flag_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-1on1-Joust.xRedFlagBase0, end: CTF-1on1-Joust.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-1on1-Joust.xRedFlagBase0",      "CTF-1on1-Joust.xBlueFlagBase0",    1,                        true)
		);
	}

        @Test
	public void test194_flag_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-1on1-Joust.xRedFlagBase0, end: CTF-1on1-Joust.xBlueFlagBase0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-1on1-Joust.xRedFlagBase0",      "CTF-1on1-Joust.xBlueFlagBase0",    20,                        true)
		);
	}

}
