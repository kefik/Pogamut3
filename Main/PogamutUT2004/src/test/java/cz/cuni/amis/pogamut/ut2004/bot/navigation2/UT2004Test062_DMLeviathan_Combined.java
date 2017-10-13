package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test062_DMLeviathan_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Leviathan";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test62_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Leviathan.PlayerStart0, end: DM-Leviathan.InventorySpot80 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Leviathan.PlayerStart0",      "DM-Leviathan.InventorySpot80",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test62_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Leviathan.PlayerStart0, end: DM-Leviathan.InventorySpot80 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Leviathan.PlayerStart0",      "DM-Leviathan.InventorySpot80",    20,                        true)
		);
	}

}
