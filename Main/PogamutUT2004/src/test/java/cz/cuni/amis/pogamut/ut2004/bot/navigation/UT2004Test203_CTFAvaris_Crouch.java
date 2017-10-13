package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test203_CTFAvaris_Crouch extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Avaris";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test203_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Avaris.PlayerStart15, end: CTF-Avaris.InventorySpot378 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.PlayerStart15",      "CTF-Avaris.InventorySpot378",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test203_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Avaris.PlayerStart15, end: CTF-Avaris.InventorySpot378 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Avaris.PlayerStart15",      "CTF-Avaris.InventorySpot378",    20,                        true)
		);
	}

}