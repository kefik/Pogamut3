package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test245_CTFDoubleDammage_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DoubleDammage";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test245_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DoubleDammage.PathNode102, end: CTF-DoubleDammage.InventorySpot281 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode102",      "CTF-DoubleDammage.InventorySpot281",    1,                        true)
		);
	}

        @Test
	public void test245_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: CTF-DoubleDammage.PathNode102, end: CTF-DoubleDammage.InventorySpot281 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode102",      "CTF-DoubleDammage.InventorySpot281",    20,                        true)
		);
	}

}