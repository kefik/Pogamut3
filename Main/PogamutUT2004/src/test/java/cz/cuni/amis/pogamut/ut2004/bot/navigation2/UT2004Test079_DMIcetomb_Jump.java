package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test079_DMIcetomb_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Icetomb";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test79_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Icetomb.PathNode29, end: DM-Icetomb.PathNode116 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Icetomb.PathNode29",      "DM-Icetomb.PathNode116",    1,                        true)
		);
	}

        /**
        *  TODO: Test fails
        */
        @Test
	public void test79_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Icetomb.PathNode29, end: DM-Icetomb.PathNode116 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Icetomb.PathNode29",      "DM-Icetomb.PathNode116",    20,                        true)
		);
	}

}
