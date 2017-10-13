package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test142_DMAntalus_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Antalus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test142_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Antalus.PathNode102, end: DM-Antalus.PathNode111 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.PathNode102",      "DM-Antalus.PathNode111",    1,                        true)
		);
	}

        @Test
	public void test142_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Antalus.PathNode102, end: DM-Antalus.PathNode111 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Antalus.PathNode102",      "DM-Antalus.PathNode111",    20,                        true)
		);
	}

}
