package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test231_CTFDEElecFields_JumpDownAndRun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DE-ElecFields";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test231_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DE-ElecFields.PathNode46, end: CTF-DE-ElecFields.AssaultPath5 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DE-ElecFields.PathNode46",      "CTF-DE-ElecFields.AssaultPath5",    1,                        true)
		);
	}

        @Test
	public void test231_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-DE-ElecFields.PathNode46, end: CTF-DE-ElecFields.AssaultPath5 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-DE-ElecFields.PathNode46",      "CTF-DE-ElecFields.AssaultPath5",    20,                        true)
		);
	}

}
