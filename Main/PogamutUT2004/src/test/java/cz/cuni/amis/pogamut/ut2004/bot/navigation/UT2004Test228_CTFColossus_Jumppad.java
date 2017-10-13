package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test228_CTFColossus_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Colossus";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test228_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Colossus.InventorySpot54, end: CTF-Colossus.JumpSpot60 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.InventorySpot54",      "CTF-Colossus.JumpSpot60",    1,                        false)
		);
	}

        @Test
	public void test228_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-Colossus.InventorySpot54, end: CTF-Colossus.JumpSpot60 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Colossus.InventorySpot54",      "CTF-Colossus.JumpSpot60",    20,                        false)
		);
	}

}