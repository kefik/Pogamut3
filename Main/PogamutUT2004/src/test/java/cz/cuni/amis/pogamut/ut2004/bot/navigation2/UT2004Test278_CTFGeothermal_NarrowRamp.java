package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test278_CTFGeothermal_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Geothermal";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test278_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Geothermal.LiftExit4, end: CTF-Geothermal.LiftExit0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Geothermal.LiftExit4",      "CTF-Geothermal.LiftExit0",    1,                        true)
		);
	}

        @Test
	public void test278_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-Geothermal.LiftExit4, end: CTF-Geothermal.LiftExit0 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Geothermal.LiftExit4",      "CTF-Geothermal.LiftExit0",    20,                        true)
		);
	}

}
