package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test275_CTFGeothermal_NarrowCorridor extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Geothermal";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test275_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Geothermal.LiftExit0, end: CTF-Geothermal.AIMarker29 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.LiftExit0",      "CTF-Geothermal.AIMarker29",    1,                        true)
		);
	}

        @Test
	public void test275_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-Geothermal.LiftExit0, end: CTF-Geothermal.AIMarker29 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.LiftExit0",      "CTF-Geothermal.AIMarker29",    20,                        true)
		);
	}

}