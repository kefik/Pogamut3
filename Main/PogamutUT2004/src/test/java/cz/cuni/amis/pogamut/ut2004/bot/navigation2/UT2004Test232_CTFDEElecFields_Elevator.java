package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test232_CTFDEElecFields_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DE-ElecFields";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test232_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DE-ElecFields.PathNode60, end: CTF-DE-ElecFields.LiftExit3 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DE-ElecFields.PathNode60",      "CTF-DE-ElecFields.LiftExit3",    1,                        false)
		);
	}

        @Test
	public void test232_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-DE-ElecFields.PathNode60, end: CTF-DE-ElecFields.LiftExit3 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DE-ElecFields.PathNode60",      "CTF-DE-ElecFields.LiftExit3",    20,                        false)
		);
	}

}
