package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test236_CTFDEElecFields_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DE-ElecFields";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test236_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DE-ElecFields.PathNode130, end: CTF-DE-ElecFields.LiftExit6 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode130",      "CTF-DE-ElecFields.LiftExit6",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test236_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-DE-ElecFields.PathNode130, end: CTF-DE-ElecFields.LiftExit6 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode130",      "CTF-DE-ElecFields.LiftExit6",    20,                        false)
		);
	}

}