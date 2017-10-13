package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test234_CTFDEElecFields_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DE-ElecFields";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test234_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DE-ElecFields.PathNode65, end: CTF-DE-ElecFields.InventorySpot59 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode65",      "CTF-DE-ElecFields.InventorySpot59",    1,                        true)
		);
	}

        @Test
	public void test234_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-DE-ElecFields.PathNode65, end: CTF-DE-ElecFields.InventorySpot59 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DE-ElecFields.PathNode65",      "CTF-DE-ElecFields.InventorySpot59",    20,                        true)
		);
	}

}