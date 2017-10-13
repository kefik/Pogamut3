package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test058_DMMetallurgy_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Metallurgy";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMetch";
	}

        @Test
	public void test58_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Metallurgy.PathNode2, end: DM-Metallurgy.LiftExit5 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Metallurgy.PathNode2",      "DM-Metallurgy.LiftExit5",    1,                        true)
		);
	}

        @Test
	public void test58_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Metallurgy.PathNode2, end: DM-Metallurgy.LiftExit5 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Metallurgy.PathNode2",      "DM-Metallurgy.LiftExit5",    20,                        true)
		);
	}

}
