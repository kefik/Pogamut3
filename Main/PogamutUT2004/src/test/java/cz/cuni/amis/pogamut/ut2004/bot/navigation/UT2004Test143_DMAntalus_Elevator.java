package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test143_DMAntalus_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Antalus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test143_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Antalus.PathNode112, end: DM-Antalus.PathNode24 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Antalus.PathNode112",      "DM-Antalus.PathNode24",    1,                        true)
		);
	}

        @Test
	public void test143_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Antalus.PathNode112, end: DM-Antalus.PathNode24 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Antalus.PathNode112",      "DM-Antalus.PathNode24",    20,                        true)
		);
	}

}