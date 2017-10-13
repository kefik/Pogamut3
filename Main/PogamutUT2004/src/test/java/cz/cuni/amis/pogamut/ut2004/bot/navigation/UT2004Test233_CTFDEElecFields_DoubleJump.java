package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test233_CTFDEElecFields_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DE-ElecFields";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test233_doublejump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DE-ElecFields.PathNode15, end: CTF-DE-ElecFields.JumpSpot1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode15",      "CTF-DE-ElecFields.JumpSpot1",    1,                        false)
		);
	}

        @Test
	public void test233_doublejump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-DE-ElecFields.PathNode15, end: CTF-DE-ElecFields.JumpSpot1 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode15",      "CTF-DE-ElecFields.JumpSpot1",    20,                        false)
		);
	}

}